package fr.octopiastudios.api;

import fr.octopiastudios.api.citizens.CitizenManager;
import fr.octopiastudios.api.config.APIConfig;
import fr.octopiastudios.api.integrations.IntegrationManager;
import fr.octopiastudios.api.logs.LogsManager;
import fr.octopiastudios.api.modules.ModuleManager;
import fr.octopiastudios.api.modules.commands.ModuleCommand;
import fr.octopiastudios.api.scoreboard.ScoreboardManager;
import fr.octopiastudios.api.utils.menu.MenuManager;
import fr.octopiastudios.api.utils.utilities.BungeeUtils;
import lombok.Getter;

@Getter
public class OSAPI extends OSPlugin {

    @Getter
    public static OSAPI API;

    private final APIConfig apiConfig;

    public OSAPI() {
        super("OCS-API", 23089);
        API = this;

        /**
         * Added 24/07/2024
         * Create a new instance of the APIConfig class
         */
        this.apiConfig = new APIConfig();
        this.apiConfig.loadAndSave();
        this.setAutoUpdate(this.apiConfig.autoUpdate);
    }

    public void registerManagers() {
        ModuleManager moduleManager = this.getModuleManager();

        moduleManager.registerManager(new ScoreboardManager(this));
        moduleManager.registerManager(new LogsManager(this));
        moduleManager.registerManager(new CitizenManager(this));
        moduleManager.registerManager(new IntegrationManager(this));

        new BungeeUtils(this);
    }

    public void registerOthers() {
        this.registerCommand(new ModuleCommand());
        this.registerListener(new MenuManager(this));
    }
}
