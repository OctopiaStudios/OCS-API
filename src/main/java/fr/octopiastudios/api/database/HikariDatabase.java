package fr.octopiastudios.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.database.debug.HikariDatabaseMetricsAnalyser;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class HikariDatabase {

    private static final Logger LOGGER = Logger.getLogger(HikariDatabase.class.getName());

    // CONFIGURATION
    private final String poolName;
    private HikariDataSource dataSource;
    private final String host, database, username, password;
    private final DatabaseType type;
    private final int port;

    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
    private static final int MAXIMUM_POOL_SIZE = (Runtime.getRuntime().availableProcessors() * 2) + 1;
    private static final int MINIMUM_IDLE = Math.min(MAXIMUM_POOL_SIZE, 10);

    private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private static final long LEAK_DETECTION_THRESHOLD = TimeUnit.SECONDS.toMillis(60);

    private HikariDatabaseMetricsAnalyser metricsAnalyser;

    public HikariDatabase(String poolName, DatabaseCredentials credentials) {
        this(poolName, credentials.getHost(), credentials.getPort(), credentials.getDatabase(), credentials.getUsername(), credentials.getPassword(), credentials.getType());
    }

    public HikariDatabase(String poolName, String host, int port, String database, String username, String password, DatabaseType type) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.type = type;
        this.poolName = poolName;
        this.setupDataSource();
    }

    private void setupDataSource() {
        HikariConfig config = new HikariConfig();
        String jdbcUrl;
        Map<String, String> properties = new HashMap<>();

        switch (type) {
            case MARIADB:
            case MYSQL:
                jdbcUrl = String.format("%s://%s:%d/%s", type.getJdbcPrefix(), host, port, database);

                properties.put("useSSL", "false");

                // Ensure we use utf8 encoding
                properties.put("useUnicode", "true");
                properties.put("characterEncoding", "utf8");

                // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
                properties.put("cachePrepStmts", "true");
                properties.put("prepStmtCacheSize", "250");
                properties.put("prepStmtCacheSqlLimit", "2048");
                properties.put("useServerPrepStmts", "true");
                properties.put("useLocalSessionState", "true");
                properties.put("rewriteBatchedStatements", "true");
                properties.put("cacheResultSetMetadata", "true");
                properties.put("cacheServerConfiguration", "true");
                properties.put("elideSetAutoCommits", "true");
                properties.put("maintainTimeStats", "false");
                properties.put("alwaysSendSetIsolation", "false");
                properties.put("cacheCallableStmts", "true");

                // Set the driver level TCP socket timeout
                properties.put("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
                break;

            case POSTGRESQL:
                try {
                    Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                jdbcUrl = String.format("%s://%s:%d/%s", type.getJdbcPrefix(), host, port, database);
                config.setDriverClassName("org.postgresql.Driver");

                // Properties specific to PostgreSQL
                properties.put("ssl", "false");
                properties.put("charSet", "UTF8");

                // Recommended settings for PostgreSQL
                properties.put("prepareThreshold", "3");
                properties.put("preparedStatementCacheQueries", "256");
                properties.put("preparedStatementCacheSizeMiB", "5");
                properties.put("databaseMetadataCacheFields", "65536");
                properties.put("databaseMetadataCacheFieldsMiB", "5");
                break;

            default:
                throw new UnsupportedOperationException("unsupported database type : " + type);
        }

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        config.setMinimumIdle(MINIMUM_IDLE);

        config.setMaxLifetime(MAX_LIFETIME);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD);

        for (Map.Entry<String, String> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }

        this.dataSource = new HikariDataSource(config);

        /**
         * @UPDATE 07/10/2024 - https://octopiagames.atlassian.net/browse/WYN-128
         * Creation of a utility class to analyze the various connections in the pool. It can be used to detect connection leaks.
         * This class is only enabled in debug mode. (activeDatabaseDebug in the APIConfig class)
         */
        if (OSAPI.getAPI().getApiConfig().activeDatabaseDebug) {
            System.out.println("[HikariDatabase - " + this.poolName + "] MAXIMUM_POOL_SIZE: " + MAXIMUM_POOL_SIZE);
            System.out.println("[HikariDatabase - " + this.poolName + "] MINIMUM_IDLE: " + MINIMUM_IDLE);
            System.out.println("[HikariDatabase - " + this.poolName + "] MAX_LIFETIME: " + MAX_LIFETIME);
            System.out.println("[HikariDatabase - " + this.poolName + "] CONNECTION_TIMEOUT: " + CONNECTION_TIMEOUT);
            System.out.println("[HikariDatabase - " + this.poolName + "] LEAK_DETECTION_THRESHOLD: " + LEAK_DETECTION_THRESHOLD);
            this.metricsAnalyser = new HikariDatabaseMetricsAnalyser(this.dataSource);
        }
    }

    /**
     * Get a connection from the pool.
     *
     * @return Connection to the database
     * @throws SQLException If an SQL error occurs
     */
    public Connection getConnection() throws SQLException {
        if (this.dataSource == null || this.dataSource.isClosed()) this.initPool();
        return this.dataSource == null ? null : this.dataSource.getConnection();
    }

    /**
     * Initialize the connection pool.
     */
    public void initPool() {
        this.setupDataSource();
    }

    /**
     * Close the connection pool.
     */
    public void closePool() {
        if (this.dataSource != null && !this.dataSource.isClosed()) {
            this.dataSource.close();
        }
    }

    /**
     * Check if the connection is valid.
     *
     * @return true if connected and valid, false otherwise
     */
    public boolean isConnected() {
        try (Connection connection = this.getConnection()) {
            return !connection.isClosed() && connection.isValid(1);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error verifying database connection", exception);
            return false;
        } finally {
            this.logPoolMetrics("isConnected()");
        }
    }

    /**
     * Execute a SQL query without parameters.
     *
     * @param calledBy For logging purposes (who called this method)
     * @param query    The SQL query to execute
     */
    public void executeQuery(String calledBy, String query) {
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error during query execution", exception);
        } finally {
            this.logPoolMetrics(calledBy);
        }
    }

    /**
     * Execute a SQL query and return the result as a Map.
     * This method does not return a ResultSet to avoid connection leaks.
     *
     * @param calledBy For logging purposes (who called this method)
     * @param query    The SQL query to execute
     * @return List<Map < String, Object>> containing the results or null in case of an error
     */
    public List<Map<String, Object>> executeSelectQuery(String calledBy, String query) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error during query execution", ex);
            return null;
        } finally {
            this.logPoolMetrics(calledBy);
        }
        return results;
    }

    /**
     * Execute an SQL update query.
     *
     * @param calledBy For logging purposes (who called this method)
     * @param query    The SQL query to execute
     */
    public void update(String calledBy, String query) {
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during query execution", e);
        } finally {
            this.logPoolMetrics(calledBy);
        }
    }

    /**
     * Verify if a table exists in the database.
     *
     * @param calledBy Who called this method (for logging)
     * @param table    The name of the table
     * @return true if the table exists, false otherwise
     */
    public boolean tableExists(String calledBy, String table) {
        String query = "SELECT 1 FROM " + table + " LIMIT 1";
        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking table existence", ex);
            return false;
        } finally {
            this.logPoolMetrics(calledBy);
        }
    }

    /**
     * Verify if a record exists in a table.
     *
     * @param calledBy Who called this method (for logging)
     * @param table    The name of the table
     * @param column   The column to check
     * @param data     The data to check
     * @return true if the record exists, false otherwise
     */
    public boolean exists(String calledBy, String table, String column, String data) {
        String query = String.format("SELECT 1 FROM %s WHERE %s = ?", table, column);
        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, data);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error verifying existence of record", ex);
            return false;
        } finally {
            this.logPoolMetrics(calledBy);
        }
    }

    /**
     * Count the number of rows in a specific table.
     *
     * @param calledBy Who called this method (for logging)
     * @param table    The name of the table
     * @return The number of rows in the table, or 0 in case of an error
     */
    public int countRows(String calledBy, String table) {
        String query = String.format("SELECT COUNT(*) FROM %s", table);
        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Line counting error", ex);
        } finally {
            this.logPoolMetrics(calledBy);
        }
        return 0;
    }

    /**
     * Create a table if it does not already exist.
     *
     * @param calledBy  Who called this method (for logging)
     * @param tablename Name of the table to create
     * @param values    The column definitions for the table
     */
    public void createTable(String calledBy, String tablename, String... values) {
        StringBuilder stmt = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tablename + " (");
        for (int i = 0; i < values.length; i++) {
            stmt.append(values[i]);
            if (i < values.length - 1) {
                stmt.append(", ");
            }
        }
        stmt.append(");");
        executeQuery(calledBy, stmt.toString());
    }

    /**
     * Delete a table if it exists.
     *
     * @param calledBy  Who called this method (for logging)
     * @param tablename The name of the table to delete
     */
    public void deleteTable(String calledBy, String tablename) {
        String sql = "DROP TABLE IF EXISTS " + tablename;
        executeQuery(calledBy, sql);
    }

    /**
     * Reset a table by deleting it and then recreating it with new definitions.
     *
     * @param calledBy  Who called this method (for logging)
     * @param tablename The name of the table to reset
     * @param values    The new column definitions for the table
     */
    public void resetTable(String calledBy, String tablename, String[] values) {
        deleteTable(calledBy, tablename);
        createTable(calledBy, tablename, values);
    }

    /**
     * Update columns in a table with specific conditions.
     *
     * @param calledBy   Who called this method (for logging)
     * @param table      The name of the table
     * @param columns    The columns to update
     * @param values     The new values for the columns
     * @param conditions The conditions to apply (where clause)
     */
    public void updateAll(String calledBy, String table, String[] columns, Object[] values, String conditions) {
        StringBuilder statement = new StringBuilder("UPDATE " + table + " SET ");
        for (int i = 0; i < columns.length; i++) {
            statement.append(columns[i]).append(" = ?,");
        }
        statement.deleteCharAt(statement.length() - 1); // remove the last comma
        statement.append(" WHERE ").append(conditions).append(";");
        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(statement.toString())) {

            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating table", ex);
        } finally {
            this.logPoolMetrics(calledBy);
        }
    }

    /**
     * Insert or update records in a table.
     *
     * @param calledBy Who called this method (for logging)
     * @param table    The name of the table
     * @param columns  Columns to insert or update
     * @param values   Values to insert or update
     * @param update   Whether to update the record if it already exists
     */
    public void insertIntoOrUpdate(String calledBy, String table, String[] columns, Object[] values, boolean update) {
        StringBuilder statement = new StringBuilder("INSERT INTO " + table + " (");
        StringBuilder placeholders = new StringBuilder("(");
        for (int i = 0; i < columns.length; i++) {
            statement.append(columns[i]);
            placeholders.append("?");
            if (i < columns.length - 1) {
                statement.append(", ");
                placeholders.append(", ");
            }
        }
        statement.append(") VALUES ").append(placeholders).append(")");

        if (update) {
            statement.append(" ON DUPLICATE KEY UPDATE ");
            for (int i = 0; i < columns.length; i++) {
                statement.append(columns[i]).append(" = VALUES(").append(columns[i]).append("), ");
            }
            statement.delete(statement.length() - 2, statement.length()); // remove the last comma and space
        }

        statement.append(";");

        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(statement.toString())) {

            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) values[i]);
                } else {
                    ps.setObject(i + 1, values[i]);
                }
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error during insertion or update", ex);
        } finally {
            this.logPoolMetrics(calledBy);
        }
    }

    /**
     * Execute a SQL query with parameters.
     *
     * @param calledBy Who called this method (for logging)
     * @param query    The SQL query to execute
     * @param params   The parameters to insert into the query
     */
    public void executeQueryWithParams(String calledBy, String query, Object... params) {
        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.execute();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error when executing query with parameters", exception);
        } finally {
            this.logPoolMetrics(calledBy);
        }
    }

    /**
     * Execute a SQL query with parameters and return the results as a Map.
     * This method does not return a ResultSet to avoid connection leaks.
     *
     * @param calledBy Who called this method (for logging)
     * @param query    The SQL query to execute
     * @param params   The parameters to insert into the query
     * @return List<Map < String, Object>> containing the results or null in case of an error
     */
    public List<Map<String, Object>> queryWithParams(String calledBy, String query, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet resultSet = ps.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error when executing query with parameters", ex);
            return null;
        } finally {
            this.logPoolMetrics(calledBy);
        }
        return results;
    }

    /**
     * Execute a SQL script from a file.
     *
     * @param filePath The path to the SQL script file
     */
    public void runSQLScript(String filePath) {
        try {
            // Read the SQL file content as a string
            String script = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

            // Split the script into individual statements using semicolon as a separator
            String[] statements = script.split("(?<!\\\\);"); // This regex avoids splitting on escaped semicolons

            // Get a connection to the database
            try (Connection connection = this.getConnection()) {
                // Disable auto-commit to manually manage transactions
                connection.setAutoCommit(false);

                try (Statement stmt = connection.createStatement()) {
                    for (String statement : statements) {
                        String sql = statement.trim();
                        if (!sql.isEmpty()) {
                            stmt.execute(sql);
                        }
                    }
                    // Commit the transaction if all statements were successfully executed
                    connection.commit();
                } catch (SQLException e) {
                    // Rollback the transaction in case of an error
                    connection.rollback();
                    LOGGER.log(Level.SEVERE, "Error executing SQL script, transaction rolled back", e);
                    throw e; // Re-throw the exception if necessary
                } finally {
                    // Re-enable auto-commit
                    connection.setAutoCommit(true);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading SQL script file", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during SQL script execution", e);
        }
    }

    /**
     * Log the metrics of the pool
     */
    public void logPoolMetrics(String requestCalledBy) {
        if (this.metricsAnalyser == null) return;
        this.metricsAnalyser.logPoolMetrics(this.poolName, requestCalledBy);
    }
}

