package fr.octopiastudios.api.utils.menu.actions;

import org.bukkit.event.inventory.InventoryCloseEvent;

public abstract class CloseAction {
    public abstract void onClose(InventoryCloseEvent event);
}

