package fr.octopiastudios.api.modules.objects;

import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.OSPlugin;
import fr.octopiastudios.api.citizens.CitizenManager;
import fr.octopiastudios.api.citizens.objects.Citizen;
import fr.octopiastudios.api.commands.CommandFramework;
import fr.octopiastudios.api.commands.ICommand;
import fr.octopiastudios.api.utils.utilities.Utils;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.io.File;

@Getter
@Setter
public abstract class Module {

    private OSPlugin plugin;

    private boolean active, desactivable;
    private String moduleName, dependencyRequiredClass;

    public Module(OSPlugin plugin, String moduleName, String dependencyRequiredClass) {
        this.plugin = plugin;
        this.active = true;
        this.desactivable = true;
        this.moduleName = moduleName;
        this.dependencyRequiredClass = dependencyRequiredClass;

        if (this.dependencyRequiredClass != null && !Utils.isClassLoaded(this.dependencyRequiredClass)) {
            this.setActive(false);
            this.getPlugin().getLogger().warning("The module " + this.moduleName + " was desactivated because the needed dependency is not on you're server.");
        }
    }

    public Module(OSPlugin plugin, String moduleName) {
        this(plugin, moduleName, null);
    }

    public void onLoad() {
        if (!this.isActive()) return;
        this.getPlugin().getLogger().info("The module " + this.moduleName + " was successfully loaded.");
    }

    public void onUnload() {
        if (!this.isActive()) return;
        this.getPlugin().getLogger().info("The module " + this.moduleName + " was successfully unloaded.");
    }

    public void registerCommand(ICommand command) {
        if (!this.isActive()) return;
        CommandFramework framework = this.plugin.getFramework();
        framework.registerCommands(command);
    }

    public void registerListener(Listener listener) {
        if (!this.isActive()) return;
        this.plugin.registerListener(listener);
    }

    public void registerCitizen(Citizen citizen) {
        if (!this.isActive()) return;
        CitizenManager citizenManager = CitizenManager.getInstance();
        citizenManager.registerCitizen(citizen);
    }

    public void registerPlaceHolder(Class<? extends PlaceholderExpansion> placeHolder) {
        if (!this.isActive()) return;
        if (!Utils.isClassLoaded("me.clip.placeholderapi.PlaceholderAPI")) return;

        try {
            placeHolder.getConstructor().newInstance().register();
        } catch (Exception e) {
            this.plugin.getLogger().severe("Error while registering the placeholder " + placeHolder.getSimpleName() + ".");
        }
    }

    public File getModuleFolder() {
        File folder = new File(this.getPlugin().getDataFolder() + File.separator + this.getClass().getSimpleName());
        if (!folder.exists()) folder.mkdir();
        return folder;
    }

    public boolean isActive(CommandSender sender) {
        if (!this.isActive()) {
            sender.sendMessage(Utils.color(OSAPI.getAPI().getApiConfig().moduleNotActivated));
            return false;
        }
        return true;
    }
}
