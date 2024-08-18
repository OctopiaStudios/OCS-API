package fr.octopiastudios.api.product;

import com.google.gson.JsonObject;
import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.OSPlugin;
import fr.octopiastudios.api.utils.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Added on 14/07/2024 - https://octopiagames.atlassian.net/browse/OCS-7
 * Notify the user if a new version of an OSPlugin is available
 * Inspired by <a href="https://github.com/Mantic-Development/ManticLib/blob/master/src/main/java/me/fullpage/manticlib/Versionator.java">ManticLib</a>
 */
public class VersionUpdater {

    private final OSPlugin plugin;
    private final String pluginName, currentVersion;
    private final String latestVersion;

    private final boolean autoUpdate;
    private boolean isCleaned;

    public VersionUpdater(OSPlugin plugin) {
        this.plugin = plugin;
        this.pluginName = plugin.getPluginName();
        this.autoUpdate = plugin.isAutoUpdate();

        this.currentVersion = plugin.getDescription().getVersion();
        this.latestVersion = this.getLatestVersion();
        this.isCleaned = false;

        /**
         * Update the plugin to the latest version
         */
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this::scanUpdates);
    }

    /**
     * Update the plugin to the latest version if available
     */
    private void scanUpdates() {
        final File directory = new File("plugins");
        if (!directory.isDirectory()) throw new IllegalArgumentException("Directory is null or not a directory");

        if (!this.isCleaned) {
            this.cleanOldFiles();
            this.isCleaned = true;
        }

        if (this.latestVersion == null) return;

        /**
         * Check if the server already has the latest version
         */
        if (Utils.convertVersion(this.currentVersion) >= Utils.convertVersion(this.latestVersion)) return;

        /**
         * Check if the plugin is already downloaded
         */
        File targetFile = new File(directory, this.pluginName + "-" + this.latestVersion + ".jar");
        if (targetFile.exists()) return;

        /**
         * Download the latest version of the plugin if the auto update is enabled
         */
        if (this.autoUpdate) this.downloadUpdate(targetFile);
        else {
            /**
             * Notify the user that a new version is available if the auto update is disabled (force the user to update for OCS-API)
             */
            this.plugin.getLogger().warning("New version of the plugin is available. You should update to the " + this.latestVersion + " version.");
            this.plugin.getLogger().warning("You can download it on Octopia Studios Discord : https://studios.octopiagames.fr/.");
        }
    }

    private void downloadUpdate(File targetFile) {
        String url = "https://files.octopiagames.fr/products/" + this.pluginName + "-" + this.latestVersion + ".jar ";
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (Throwable e) {
            targetFile.delete();
            plugin.getLogger().warning("An error occurred while downloading the latest version (" + this.latestVersion + ")");
            return;
        }

        this.isCleaned = false;
        plugin.getLogger().info("Downloaded the latest version " + this.latestVersion);
        plugin.getLogger().warning("Please restart the server to use the new version");

        try {
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File file = (File) getFileMethod.invoke(plugin);
            if (file != null) {
                file.renameTo(new File(file.getAbsolutePath() + ".old"));
            }
        } catch (Throwable ignored) {
        }
    }

    private void cleanOldFiles() {
        final File folder = new File("plugins");
        if (!folder.isDirectory()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        Integer current = Utils.convertVersion(this.currentVersion);
        for (File file : files) {
            if (file.isFile()) {
                String lowerCase = file.getName().toLowerCase().trim();

                if (lowerCase.startsWith(this.pluginName.toLowerCase())) {
                    if (lowerCase.endsWith(".jar")) {
                        this.plugin.getLogger().info("Found jar file: " + file.getName());
                        String version = lowerCase.substring(lowerCase.indexOf("-") + 1, lowerCase.indexOf(".jar"));
                        final Integer oldVersion = Utils.convertVersion(version);
                        if (oldVersion != 0) {
                            if (current > oldVersion) {
                                plugin.getLogger().info("Deleting old file " + file.getName());
                                try {
                                    file.delete();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (lowerCase.endsWith(".old")) {
                        plugin.getLogger().info("Deleting old file " + file.getName());
                        try {
                            file.delete();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the latest version of the plugin on the remote server
     *
     * @return the remote version
     */
    private String getLatestVersion() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                String request = Utils.getContent("https://files.octopiagames.fr/products/latest_versions.json");
                JsonObject jsonObject = OSAPI.getAPI().getGson().fromJson(request, JsonObject.class);
                return jsonObject.get(this.pluginName).getAsString();
            } catch (Exception e) {
                this.plugin.getLogger().warning("Unable to check latest plugin version.");
            }
            return null;
        });
        return future.join();
    }
}