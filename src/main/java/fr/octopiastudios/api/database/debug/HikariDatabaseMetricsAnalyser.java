package fr.octopiastudios.api.database.debug;

import com.zaxxer.hikari.HikariDataSource;
import fr.octopiastudios.api.utils.utilities.Utils;

public class HikariDatabaseMetricsAnalyser {

    private final HikariDataSource dataSource;

    public HikariDatabaseMetricsAnalyser(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get actives connections on the pool
     */
    public int getActiveConnections() {
        return dataSource.getHikariPoolMXBean().getActiveConnections();
    }

    /**
     * Get total connections on the pool (active and available)
     */
    public int getTotalConnections() {
        return dataSource.getHikariPoolMXBean().getTotalConnections();
    }

    /**
     * Get idle connections on the pool
     */
    public int getIdleConnections() {
        return dataSource.getHikariPoolMXBean().getIdleConnections();
    }

    /**
     * Get pending connections on the pool
     */
    public int getPendingConnections() {
        return dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection();
    }

    /**
     * Log all metrics of the pool
     */
    public void logPoolMetrics(String poolName, String requestCalledBy) {
        System.out.println(Utils.getTitleWithoutColor("DATABASE"));
        System.out.println("DEBUG - " + poolName + " | Requested By: " + requestCalledBy);
        System.out.println("Total Connections: " + getTotalConnections());
        System.out.println("Active Connections: " + getActiveConnections());
        System.out.println("Idle Connections: " + getIdleConnections());
        System.out.println("Pending Threads: " + getPendingConnections());
        System.out.println("Pool Size: " + dataSource.getMaximumPoolSize());
        System.out.println(Utils.SIMPLE_LINE);
    }
}
