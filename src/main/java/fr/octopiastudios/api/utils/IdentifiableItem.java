package fr.octopiastudios.api.utils;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class IdentifiableItem {

    private final String uniqueId;
    private final ItemStack item;

    public IdentifiableItem(ItemStack item) {
        this.uniqueId = UUID.randomUUID().toString().substring(0, 16);
        this.item = item;
    }
}
