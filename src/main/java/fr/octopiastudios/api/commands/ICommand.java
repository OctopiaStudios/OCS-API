package fr.octopiastudios.api.commands;

import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.cooldowns.CooldownUtils;
import fr.octopiastudios.api.cooldowns.DurationFormatter;
import fr.octopiastudios.api.utils.utilities.Utils;
import org.bukkit.entity.Player;

public abstract class ICommand {

    public abstract void onCommand(CommandArgs args);

    public boolean checkCooldown(Player target, int seconds) {
        String name = getClass().getName().substring(0, 16);
        String cooldownName = name + "_COMMAND";
        if (CooldownUtils.isOnCooldown(cooldownName, target) && !target.hasPermission("core.cooldown")) {
            String cooldown = DurationFormatter.getRemaining(CooldownUtils.getCooldownForPlayerLong(cooldownName, target), true);
            target.sendMessage(Utils.color(OSAPI.getAPI().getApiConfig().commandOnCooldown.replace("{cooldown}", cooldown)));
            return false;
        }
        CooldownUtils.addCooldown(cooldownName, target, seconds);
        return true;
    }
}

