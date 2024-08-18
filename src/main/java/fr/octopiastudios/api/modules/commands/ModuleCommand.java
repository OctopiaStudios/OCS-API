package fr.octopiastudios.api.modules.commands;

import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.commands.CommandArgs;
import fr.octopiastudios.api.commands.ICommand;
import fr.octopiastudios.api.commands.annotations.Command;
import fr.octopiastudios.api.modules.ModuleManager;
import fr.octopiastudios.api.modules.objects.Module;
import fr.octopiastudios.api.utils.utilities.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ModuleCommand extends ICommand {

    @Command(name = {"module", "modules", "system"}, isConsole = true)
    public void onCommand(CommandArgs args) {
        CommandSender sender = args.getSender();
        this.openModules(sender);
    }

    @Command(name = {"module.toggle", "modules.toggle", "system.toggle"}, isConsole = true, permissionNode = "op")
    public void onCommandToggle(CommandArgs args) {
        CommandSender player = args.getSender();

        if (args.length() < 1) {
            player.sendMessage("§c/module toggle <type>");
            return;
        }

        Module module = OSAPI.getAPI().getModuleManager().getModuleByName(args.getArgs(0));

        if (module == null) {
            player.sendMessage("§cSorry, but the module §6" + args.getArgs(0) + " §cdoes not exist.");
            return;
        }

        if (!module.isDesactivable()) {
            player.sendMessage("§cSorry, but the module §6" + module.getModuleName() + " §cis not desactivable.");
            return;
        }

        module.setActive(!module.isActive());

        String isActive = module.isActive() ? "activated" : "desactivated";
        player.sendMessage("§aThe module " + module.getModuleName() + " is now " + isActive + ".");
    }

    public void openModules(CommandSender player) {
        ModuleManager moduleManager = OSAPI.getAPI().getModuleManager();
        List<Module> modules = moduleManager.getModules();

        player.sendMessage(Utils.getTitle("Modules"));
        player.sendMessage("§eList of modules §7(on Octopia Studios plugins):");
        for (Module module : modules) {
            if (!module.isDesactivable()) continue;
            player.sendMessage("§7- " + (module.isActive() ? "§a" : "§c") + module.getModuleName());
        }
        player.sendMessage("§7§oModules are the very functionalities of a plugin. In the event of problems, they can be deactivated until a solution is found.");
        player.sendMessage(Utils.GOLD_LINE);
    }
}
