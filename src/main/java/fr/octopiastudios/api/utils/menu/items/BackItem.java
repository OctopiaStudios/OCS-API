package fr.octopiastudios.api.utils.menu.items;

import fr.octopiastudios.api.utils.menu.ItemBuilder;
import fr.octopiastudios.api.utils.menu.MenuManager;
import fr.octopiastudios.api.utils.menu.VirtualGUI;
import fr.octopiastudios.api.utils.menu.actions.ClickAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BackItem extends VirtualItem {
    public BackItem() {
        super(new ItemBuilder(Material.ARROW).displayname("&c&lRetour").build());
        this.onItemClick(new ClickAction() {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                VirtualGUI gui = (VirtualGUI) MenuManager.getInstance().getGuis().get(player.getUniqueId());
                if (gui != null) {
                    gui.open(player);
                }
            }
        });
    }

}

