package fr.octopiastudios.api.citizens.objects;

import fr.octopiastudios.api.citizens.CitizenUtils;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class Citizen {

    @Setter
    private String alias, name;
    private final EntityType type;
    private final boolean onlyOne;

    public Citizen(String alias, String name, EntityType type) {
        this(alias, name, type, false);
    }

    public Citizen(String alias, String name, EntityType type, boolean onlyOne) {
        this.alias = alias;
        this.name = name;
        this.type = type;
        this.onlyOne = onlyOne;
    }

    public abstract void onCitizenWasClicked(Player player, NPC npc, ClickType type);

    public void spawnCitizen(Location location) {
        NPC currentNpc = CitizenUtils.createNpc(this.name, this.type, location);
        currentNpc.data().setPersistent(this.alias, true);
    }

    public List<NPC> getNPC() {
        return CitizenUtils.getNPCByAlias(this.alias);
    }
}
