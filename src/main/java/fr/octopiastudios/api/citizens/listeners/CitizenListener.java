package fr.octopiastudios.api.citizens.listeners;

import fr.octopiastudios.api.citizens.CitizenManager;
import fr.octopiastudios.api.citizens.objects.Citizen;
import fr.octopiastudios.api.citizens.objects.ClickType;
import net.citizensnpcs.api.event.NPCDamageEntityEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class CitizenListener implements Listener {

    private final CitizenManager citizenManager = CitizenManager.getInstance();

    @EventHandler
    public void onCitizenRightClick(NPCRightClickEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getClicker();
        NPC npc = event.getNPC();
        List<Citizen> citizens = this.citizenManager.getCitizens();
        citizens.forEach(citizen -> {
            String alias = citizen.getAlias();
            if (npc.data().has(alias)) citizen.onCitizenWasClicked(player, npc, ClickType.RIGHT);
        });
    }

    @EventHandler
    public void onCitizenLeftClick(NPCLeftClickEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getClicker();
        NPC npc = event.getNPC();
        List<Citizen> citizens = this.citizenManager.getCitizens();
        citizens.forEach(citizen -> {
            String alias = citizen.getAlias();
            if (npc.data().has(alias)) citizen.onCitizenWasClicked(player, npc, ClickType.LEFT);
        });
    }

    @EventHandler
    public void onDamage(NPCDamageEntityEvent event) {
        event.setCancelled(true);
    }
}
