package fr.octopiastudios.api.citizens;

import com.google.common.collect.Lists;
import fr.octopiastudios.api.OSPlugin;
import fr.octopiastudios.api.citizens.commands.CitizenCreateCommand;
import fr.octopiastudios.api.citizens.commands.CitizenListCommand;
import fr.octopiastudios.api.citizens.listeners.CitizenListener;
import fr.octopiastudios.api.citizens.objects.Citizen;
import fr.octopiastudios.api.modules.objects.Module;
import lombok.Getter;

import java.util.List;

@Getter
public class CitizenManager extends Module {

    @Getter
    private static CitizenManager instance;
    private final List<Citizen> citizens;

    public CitizenManager(OSPlugin plugin) {
        super(plugin, "Citizens", "net.citizensnpcs.Citizens");
        instance = this;
        this.citizens = Lists.newArrayList();

        // COMMANDS
        this.registerCommand(new CitizenCreateCommand());
        this.registerCommand(new CitizenListCommand());

        // LISTENERS
        this.registerListener(new CitizenListener());
    }

    public void registerCitizen(Citizen citizen) {
        this.citizens.add(citizen);
    }

    public Citizen getCitizen(String alias) {
        if (this.citizens.isEmpty()) return null;
        return this.citizens.stream().filter(citizen -> citizen.getAlias().equalsIgnoreCase(alias)).findFirst().orElse(null);
    }
}
