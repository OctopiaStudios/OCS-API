package fr.octopiastudios.api.tasks;

import fr.octopiastudios.api.OSPlugin;
import org.bukkit.Bukkit;

public enum TaskRunner {

    SYNC {
        public void runTask(Runnable runnable, OSPlugin plugin) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable);
        }

        public void runTaskWithDelay(Runnable runnable, long delay, OSPlugin plugin) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
        }

        public void runRepetatingTask(Runnable runnable, long period, long delay, OSPlugin plugin) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, period, delay);
        }
    },
    ASYNC {
        @SuppressWarnings("deprecation")
        public void runTask(Runnable runnable, OSPlugin plugin) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, runnable);
        }

        @SuppressWarnings("deprecation")
        public void runTaskWithDelay(Runnable runnable, long delay, OSPlugin plugin) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, runnable, delay);
        }

        @SuppressWarnings("deprecation")
        public void runRepetatingTask(Runnable runnable, long period, long delay, OSPlugin plugin) {
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, runnable, period, delay);
        }
    };

    public void runTask(Runnable runnable, OSPlugin plugin) {
        throw new AbstractMethodError("not AUTHORIZED");
    }

    public void runTaskWithDelay(Runnable runnable, long delay, OSPlugin plugin) {
        throw new AbstractMethodError("not AUTHORIZED");
    }

    public void runRepetatingTask(Runnable runable, long period, long delay, OSPlugin plugin) {
        throw new AbstractMethodError("not AUTHORIZED");
    }
}
