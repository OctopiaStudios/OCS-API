package fr.octopiastudios.api.citizens;

import com.google.common.collect.Lists;
import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.utils.utilities.Utils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class CitizenUtils {

    private static final NPCRegistry citizensRegistry = CitizensAPI.getNPCRegistry();

    public static NPC createNpc(String name, EntityType type, Location location) {
        NPC npc = citizensRegistry.createNPC(type, "-");
        npc.spawn(location);
        npc.setName(Utils.color(name));

        if (npc.getEntity() != null && npc.getEntity().getCustomName() != null)
            npc.getEntity().setCustomNameVisible(true);

        npc.getEntity().setMetadata("npc", new FixedMetadataValue(OSAPI.getAPI(), npc));
        return npc;
    }

    public static void removeNPC(int id) {
        NPC npc = CitizenUtils.getNPC(id);
        if (npc == null) {
            return;
        }
        citizensRegistry.deregister(npc);
    }

    public static NPC getNPC(int id) {
        return citizensRegistry.getById(id);
    }

    public static List<NPC> getAllNPC() {
        return Lists.newArrayList(citizensRegistry.iterator());
    }

    public static List<NPC> getNPCByAlias(String alias) {
        List<NPC> npcs = Lists.newArrayList();
        List<NPC> allNpcs = getAllNPC();
        allNpcs.stream().filter(npc -> npc.data().has(alias)).forEach(npcs::add);
        return npcs;
    }
}
