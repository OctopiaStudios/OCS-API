package fr.octopiastudios.api.integrations;

import com.google.common.collect.Lists;
import fr.octopiastudios.api.logs.LogType;
import fr.octopiastudios.api.logs.LogsManager;
import fr.octopiastudios.api.utils.utilities.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;

@Getter
@Setter
public abstract class Integration {

    private final String pluginName;
    private final List<String> requiredClasses;
    private String minimumVersion;
    private boolean isEnabled;

    public Integration(String pluginName, String... requiredClasses) {
        this.pluginName = pluginName;
        this.requiredClasses = Lists.newArrayList(requiredClasses);
        this.minimumVersion = null;
        this.enableIntegration();
    }

    public abstract void onLoad();

    public abstract void onUnload();

    public void enableIntegration() {
        boolean isLoaded = this.tryToLoad();
        if (!isLoaded) {
            Bukkit.getLogger().warning("Failed to load the integration " + this.pluginName + " because the required classes are not loaded.");
            return;
        }

        this.isEnabled = true;
        this.onLoad();

        LogsManager.sendConsole("", "The Integration " + this.pluginName + " was successfully enabled.", LogType.SUCCESS);
    }

    public void disableIntegration() {
        if (!this.isEnabled) return;
        this.isEnabled = false;
        this.onUnload();

        LogsManager.sendConsole("", "The Integration " + this.pluginName + " was successfully disabled.", LogType.SUCCESS);
    }

    public boolean tryToLoad() {
        /**
         * Check if the required classes are loaded
         */
        boolean isClassesLoaded = true;
        for (String requiredClass : this.requiredClasses) {
            if (!Utils.isClassLoaded(requiredClass)) {
                isClassesLoaded = false;
                break;
            }
        }

        boolean isEnabled = Utils.isEnabled(this.pluginName);

        /**
         * If the class is loaded but the plugin is not (i.e. the plugin has been shade somewhere) we return true.
         */
        if (isClassesLoaded && !isEnabled) return true;

        /**
         * In the other case, if the classes aren't loaded, and the plugin isn't loaded either, that means the plugin simply isn't there.
         */
        if (!isEnabled) return false;

        /**
         * If it's the plugin (and not the classes) that's loaded, we want to check that the version corresponds to the minimum registered version.
         */
        Plugin plugin = Utils.getPlugin(this.pluginName);
        return plugin != null && this.isAtLeastMinimumVersion(plugin);
    }

    private boolean isAtLeastMinimumVersion(Plugin plugin) {
        if (minimumVersion == null) return true;
        return Utils.convertVersion(plugin.getDescription().getVersion()) >= Utils.convertVersion(minimumVersion);
    }
}
