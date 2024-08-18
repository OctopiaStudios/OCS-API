package fr.octopiastudios.api;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.octopiastudios.api.bstats.Metrics;
import fr.octopiastudios.api.commands.CommandFramework;
import fr.octopiastudios.api.modules.ModuleManager;
import fr.octopiastudios.api.modules.objects.Module;
import fr.octopiastudios.api.product.MisinformProduct;
import fr.octopiastudios.api.product.ProductInformations;
import fr.octopiastudios.api.product.UnversionedProduct;
import fr.octopiastudios.api.product.VersionUpdater;
import fr.octopiastudios.api.saveable.EnumTypeAdapter;
import fr.octopiastudios.api.saveable.FilePersist;
import fr.octopiastudios.api.saveable.adapters.ItemStackAdapter;
import fr.octopiastudios.api.saveable.adapters.LocationAdapter;
import fr.octopiastudios.api.saveable.adapters.PotionEffectAdapter;
import lombok.Getter;
import lombok.Setter;
import me.fullpage.nmslib.NMSHandler;
import me.fullpage.nmslib.plugin.NMSLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Getter
public abstract class OSPlugin extends JavaPlugin {

    private final String pluginName;

    private ModuleManager moduleManager;
    private CommandFramework framework;
    private NMSHandler nmsHandler;

    private Gson gson;
    private List<FilePersist> persists;

    private final int metricsId;
    @Setter
    private boolean autoUpdate;

    public OSPlugin(String pluginName) {
        this(pluginName, -1);
    }

    public OSPlugin(String pluginName, int metricsId) {
        this.pluginName = pluginName;
        this.metricsId = metricsId;
        this.autoUpdate = false;
        this.initFolderAndGson();
    }

    public void onEnable() {
        /**
         * Added on 23/07/2024 - https://octopiagames.atlassian.net/browse/OCS-8
         * Integrate bStats for plugin metrics
         * If the metricsId is -1, the plugin was a commissions and the metrics will not be enabled
         */
        if (this.metricsId != -1) {
            new Metrics(this, this.metricsId);
            this.getLogger().info("Metrics enabled.");
        }

        /**
         * Added on 23/07/2024 - https://octopiagames.atlassian.net/browse/OCS-11
         * Integrate NMSLib for NMS version compatibility (thanks Mantic)
         */
        nmsHandler = NMSLib.init(this);

        /**
         * Added on 22/07/2024
         * Send the plugin informations to the console
         */
        ProductInformations productInformations = new ProductInformations(this);

        /**
         * Load plugin managers
         */
        this.persists = Lists.newArrayList();
        this.framework = new CommandFramework(this);
        this.moduleManager = pluginName.equals("OCS-API") ? new ModuleManager() : OSAPI.getAPI().getModuleManager();

        this.registerManagers();
        this.registerOthers();
        this.loadPersists();

        this.moduleManager.getModules().stream().filter(module -> module.getPlugin().getPluginName().equals(this.getPluginName())).forEach(Module::onLoad);

        /**
         * Added on 22/07/2024
         * Send the plugin informations to the console
         */
        if (!(this instanceof MisinformProduct)) productInformations.sendInformations();

        /**
         * Added on 14/07/2024 - https://octopiagames.atlassian.net/browse/OCS-7
         * Notify the user if a new version of an OSPlugin is available
         */
        if (!(this instanceof UnversionedProduct)) new VersionUpdater(this);

        super.onEnable();
    }

    public void onDisable() {
        /**
         * Unload plugin managers
         * If the persists is null, the license is not valid and the
         */
        if (this.persists != null && this.moduleManager != null) {
            this.savePersists(true);
            this.moduleManager.getModules().stream().filter(module -> module.getPlugin().getPluginName().equals(this.getPluginName())).forEach(Module::onUnload);
        }
        super.onDisable();
    }

    public abstract void registerManagers();

    public abstract void registerOthers();

    public void registerPersists(FilePersist persist) {
        this.persists.add(persist);
    }

    public void registerListener(Listener listener) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(listener, this);
    }

    public void registerCommand(Object command) {
        this.framework.registerCommands(command);
    }

    public void loadPersists() {
        this.persists.forEach(FilePersist::loadData);
    }

    public void savePersists(boolean value) {
        this.persists.forEach(persist -> persist.saveData(value));
    }

    /**
     * Init plugin folder and gson
     * We call this function in the constructor and not in the onEnable() function, as some features of other
     * Octopia Studios plugins require this information before calling the onEnable() function.
     */
    public void initFolderAndGson() {
        /**
         * Create plugin data folder
         */
        this.getDataFolder().mkdir();

        /**
         * Create Gson instance
         */
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
                .excludeFieldsWithModifiers(128, 64).registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY)
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeAdapter(PotionEffect.class, new PotionEffectAdapter(this))
                .registerTypeAdapter(Location.class, new LocationAdapter(this))
                .create();
    }

}
