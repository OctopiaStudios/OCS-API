package fr.octopiastudios.api.utils.menu;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public class VirtualHolder implements InventoryHolder {

    private final GUI gui;
    private final Inventory inventory;
    private final int nextPage;

    public VirtualHolder(GUI gui, Inventory inventory) {
        this.gui = gui;
        this.inventory = inventory;
        this.nextPage = -1;
    }
}
