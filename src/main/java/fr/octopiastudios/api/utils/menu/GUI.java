package fr.octopiastudios.api.utils.menu;

import fr.octopiastudios.api.utils.menu.items.VirtualItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public interface GUI {

    GUI setItem(int slot, VirtualItem item);

    GUI addItem(VirtualItem item);

    void open(Player player);

    void apply(Inventory inventory, Player player);

    void onInventoryClick(InventoryClickEvent event);

    void onInventoryClose(InventoryCloseEvent event);
}

