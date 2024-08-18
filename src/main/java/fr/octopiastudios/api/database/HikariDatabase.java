package fr.octopiastudios.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class HikariDatabase {

    private static final Logger LOGGER = Logger.getLogger(HikariDatabase.class.getName());

    private final String poolName;
    private HikariDataSource dataSource;
    private final String host, database, username, password;
    private final int port;

    public HikariDatabase(String poolName, DatabaseCredentials credentials) {
        this(poolName, credentials.getHost(), credentials.getPort(), credentials.getDatabase(), credentials.getUsername(), credentials.getPassword());
    }

    public HikariDatabase(String poolName, String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.poolName = poolName;
        setupDataSource();
    }

    private void setupDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8", host, port, database));
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.setPoolName(poolName);
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(600000L);
        config.setIdleTimeout(300000L);
        config.setConnectionTimeout(10000L);

        /**
         * Configuration de la détection des fuites de mémoire
         */
        config.setLeakDetectionThreshold(2000);
        config.setValidationTimeout(3000);

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        if (this.dataSource == null || this.dataSource.isClosed()) this.initPool();
        return this.dataSource == null ? null : this.dataSource.getConnection();
    }

    public void initPool() {
        this.setupDataSource();
    }

    public void closePool() {
        this.dataSource.close();
    }

    public boolean isConnected() {
        try (Connection connection = this.getConnection()) {
            return !connection.isClosed();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error verifying database connection", exception);
            return false;
        }
    }

    public void executeQuery(String query) {
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error during query execution", exception);
        }
    }

    public ResultSet query(String query) {
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement()) {
            return statement.executeQuery(query);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error during query execution", ex);
            return null;
        }
    }

    public void update(String query) {
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during query execution", e);
        }
    }

    public boolean tableExists(String table) {
        try (Connection connection = this.getConnection();
             ResultSet rs = connection.getMetaData().getTables(null, null, table, null)) {
            return rs.next();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking table existence", ex);
            return false;
        }
    }

    public boolean exists(String table, String column, String data) {
        String query = String.format("SELECT * FROM %s WHERE %s = ?", table, column);
        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);) {
            ps.setString(1, data);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error verifying existence of record", ex);
            return false;
        }
    }

    public int countRows(String table) {
        String query = String.format("SELECT COUNT(*) FROM %s", table);
        try (ResultSet set = query(query)) {
            if (set != null && set.next()) return set.getInt(1);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Line counting error", ex);
        }
        return 0;
    }

    public void createTable(String tablename, String... values) {
        StringBuilder stmt = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tablename + " (");
        for (int i = 0; i < values.length; i++) {
            stmt.append(values[i]);
            if (i < values.length - 1) {
                stmt.append(", ");
            }
        }
        stmt.append(");");
        executeQuery(stmt.toString());
    }

    public void deleteTable(String tablename) {
        String sql = "DROP TABLE IF EXISTS " + tablename;
        executeQuery(sql);
    }

    public void resetTable(String tablename, String[] values) {
        deleteTable(tablename);
        createTable(tablename, values);
    }

    public void updateAll(String table, String[] columns, Object[] values, String conditions) {
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
        }
    }

    public void insertIntoOrUpdate(String table, String[] columns, Object[] values, boolean update) {
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
        }
    }

    // Ajout de la méthode executeQueryWithParams pour exécuter des requêtes SQL avec des paramètres
    public void executeQueryWithParams(String query, Object... params) {
        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.execute();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error when executing query with parameters", exception);
        }
    }

    // Ajout de la méthode queryWithParams pour exécuter des requêtes SQL avec des paramètres et renvoyer un ResultSet
    public ResultSet queryWithParams(String query, Object... params) {
        try (Connection connection = this.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error when executing query with parameters", ex);
            return null;
        }
    }
}

