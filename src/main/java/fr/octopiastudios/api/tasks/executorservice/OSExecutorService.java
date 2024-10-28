package fr.octopiastudios.api.tasks.executorservice;

import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.utils.utilities.Utils;
import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Getter
public class OSExecutorService {

    /**
     * MODULE_NAME-COUNT
     * Example : STORE-1
     */
    private final String name;
    private final ThreadPoolExecutor executorService;
    private final int poolSize;

    public OSExecutorService(String moduleName, int poolSize) {
        this.name = moduleName;
        this.poolSize = poolSize;
        this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
    }

    public void logExecutorService() {
        if (!OSAPI.getAPI().getApiConfig().activeThreadDebug) return;

        System.out.println(Utils.getTitleWithoutColor("THREAD"));
        System.out.println("DEBUG - " + this.name);
        System.out.println("Total Threads (active / inactive): " + this.executorService.getPoolSize());
        System.out.println("Active Connections: " + this.executorService.getActiveCount());
        System.out.println("Idle Connections: " + (this.executorService.getPoolSize() - this.executorService.getActiveCount()));
        System.out.println("Pending Threads: " + this.executorService.getQueue().size());
        System.out.println("Pool Size: " + this.executorService.getMaximumPoolSize());
        System.out.println(Utils.SIMPLE_LINE);
    }
}
