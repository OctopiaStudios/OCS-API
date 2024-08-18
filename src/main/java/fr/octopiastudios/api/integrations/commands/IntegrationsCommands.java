package fr.octopiastudios.api.integrations.commands;

import fr.octopiastudios.api.commands.CommandArgs;
import fr.octopiastudios.api.commands.ICommand;
import fr.octopiastudios.api.commands.annotations.Command;
import fr.octopiastudios.api.integrations.Integration;
import fr.octopiastudios.api.integrations.IntegrationManager;
import fr.octopiastudios.api.utils.utilities.Utils;
import org.bukkit.entity.Player;

import java.util.List;

public class IntegrationsCommands extends ICommand {

    private final IntegrationManager integrationManager = IntegrationManager.getInstance();

    @Command(name = {"integration", "integrations", "integration.list", "integrations.list"}, permissionNode = "op")
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        List<Integration> integrations = integrationManager.getIntegrations();
        player.sendMessage(Utils.getTitle("Integrations"));
        player.sendMessage("§eList of Integrations §7(on Octopia Studios plugins):");
        for (Integration integration : integrations) {
            player.sendMessage("§7- " + (integration.isEnabled() ? "§a" : "§c") + integration.getPluginName());
        }
        player.sendMessage("§7§oIntegrations are used to integrate certain functionalities of other plugins for easier use with our plugins.");
        player.sendMessage(Utils.GOLD_LINE);
    }
}
