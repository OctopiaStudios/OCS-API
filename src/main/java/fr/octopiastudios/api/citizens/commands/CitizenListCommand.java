package fr.octopiastudios.api.citizens.commands;

import fr.octopiastudios.api.citizens.CitizenManager;
import fr.octopiastudios.api.citizens.objects.Citizen;
import fr.octopiastudios.api.commands.CommandArgs;
import fr.octopiastudios.api.commands.ICommand;
import fr.octopiastudios.api.commands.annotations.Command;
import org.bukkit.entity.Player;

import java.util.List;

public class CitizenListCommand extends ICommand {

    private final CitizenManager citizenManager = CitizenManager.getInstance();

    @Command(name = {"citizen.list"}, permissionNode = "op")
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        List<Citizen> citizens = citizenManager.getCitizens();
        if (citizens.isEmpty()) {
            player.sendMessage("§cNo Citizens are registered on the server.");
            return;
        }

        for (Citizen citizen : citizens) player.sendMessage("§7- " + citizen.getAlias());
    }
}
