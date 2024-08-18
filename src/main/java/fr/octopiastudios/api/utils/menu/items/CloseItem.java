package fr.octopiastudios.api.utils.menu.items;

import fr.octopiastudios.api.utils.menu.ItemBuilder;
import fr.octopiastudios.api.utils.menu.actions.ClickAction;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CloseItem extends VirtualItem {
    public CloseItem() {
        super(new ItemBuilder(Material.ACACIA_DOOR).displayname("&6&lFermer le menu").build());
        this.onItemClick(new ClickAction() {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
            }
        });
    }

}

