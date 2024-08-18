package fr.octopiastudios.api.citizens.commands;

import fr.octopiastudios.api.citizens.CitizenManager;
import fr.octopiastudios.api.citizens.objects.Citizen;
import fr.octopiastudios.api.commands.CommandArgs;
import fr.octopiastudios.api.commands.ICommand;
import fr.octopiastudios.api.commands.annotations.Command;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CitizenCreateCommand extends ICommand {

    private final CitizenManager citizenManager = CitizenManager.getInstance();

    @Command(name = {"citizen.create"}, permissionNode = "op")
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() < 1) {
            player.sendMessage("§c/citizen create <alias>");
            return;
        }

        String alias = args.getArgs(0);
        Citizen citizen = citizenManager.getCitizen(alias);

        if (citizen == null) {
            player.sendMessage("§cSorry this Citizen does not exist.");
            return;
        }

        if (citizen.isOnlyOne()) {
            player.sendMessage("§cSorry, only one Citizen of this type can be spawned!");
            return;
        }

        Location location = player.getLocation();
        citizen.spawnCitizen(location);
        player.sendMessage("§aYou have successfully spawned the Citizen §e" + citizen.getAlias() + "§a!");
    }
}
