package fr.octopiastudios.api.chances;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class ChanceLoot {

    private ItemStack item;
    private double chance;

    public ChanceLoot(ItemStack item, double chance) {
        this.item = item;
        this.chance = chance;
    }
}
