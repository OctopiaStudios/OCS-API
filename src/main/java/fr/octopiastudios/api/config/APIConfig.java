package fr.octopiastudios.api.config;

import de.exlll.configlib.BukkitYamlConfiguration;
import de.exlll.configlib.annotation.Comment;
import fr.octopiastudios.api.OSAPI;

public class APIConfig extends BukkitYamlConfiguration {

    public APIConfig() {
        super(OSAPI.getAPI(), "config");
    }

    @Comment("Auto update OCS-API if a new version is available (recommended)")
    public boolean autoUpdate = true;

    @Comment({"", "Messages"})
    public String commandNotFound = "&cThis command does not exist.";
    public String noPermissionCommand = "&cYou do not have permission to use this command.";
    public String onlyInGameCommand = "&cThis command is only available in-game!";
    public String commandOnCooldown = "&ePlease wait &c{cooldown} &ebefore using this command!";
    public String unknownError = "&cSorry, an error occurred. Please contact an Administrator and describe the action.";

    @Comment({"", "Module messages"})
    public String moduleNotActivated = "&cThis module is experiencing a problem and has been temporarily disabled.";
}
