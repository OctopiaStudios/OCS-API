package fr.octopiastudios.api.utils.menu.items;

import fr.octopiastudios.api.utils.menu.ItemBuilder;
import fr.octopiastudios.api.utils.menu.actions.ClickAction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class VirtualItem {

    private final ItemStack item;
    private ClickAction action;
    private boolean allowClick;

    public VirtualItem(ItemStack item) {
        this.item = item;
        this.action = null;
    }

    public VirtualItem(ItemBuilder item) {
        this.item = item.build();
        this.action = null;
        this.allowClick = false;
    }

    public VirtualItem onItemClick(ClickAction action) {
        this.action = action;
        return this;
    }
}
