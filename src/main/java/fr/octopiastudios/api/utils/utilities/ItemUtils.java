package fr.octopiastudios.api.utils.utilities;

import fr.octopiastudios.api.chances.ChanceLoot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class ItemUtils {

    /**
     * Check if the inventory content is empty
     *
     * @param player
     * @return
     */
    public static boolean isInventoryEmpty(Player player) {
        return isInventoryEmpty(player.getInventory());
    }

    /**
     * Check if the inventory content is empty
     *
     * @param inventory
     * @return
     */
    public static boolean isInventoryEmpty(Inventory inventory) {
        return isInventoryEmpty(inventory.getContents());
    }

    /**
     * Check if the inventory content is empty
     *
     * @param contents
     * @return
     */
    public static boolean isInventoryEmpty(ItemStack[] contents) {
        for (ItemStack item : contents) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the armor content is empty
     *
     * @param player
     * @return
     */
    public static boolean isArmorEmpty(Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the player has any item in his inventory
     * Check the inventory and the armor content
     *
     * @param player
     * @return
     */
    public static boolean hasAnyInventoryItem(Player player) {
        return !isInventoryEmpty(player) || !isArmorEmpty(player);
    }

    /**
     * Check if the player has the required item
     *
     * @param player
     * @param item
     * @return
     */
    public static boolean haveRequiredItem(Player player, ItemStack item) {
        return haveRequiredItem(player, item, 1);
    }

    /**
     * Check if the player has the required item
     *
     * @param player
     * @param item
     * @param quantity
     * @return
     */
    public static boolean haveRequiredItem(Player player, ItemStack item, int quantity) {
        int quantityInInventory = getItemCount(player, item);
        return quantityInInventory >= quantity;
    }

    /**
     * Check if the player inventory was full
     *
     * @param player
     * @return
     */
    public static boolean isFullInventory(Player player) {
        return isFullInventory(player.getInventory());
    }

    /**
     * Check if the inventory was full
     *
     * @param inventory
     * @return
     */
    public static boolean isFullInventory(Inventory inventory) {
        for (ItemStack current : inventory.getContents()) {
            if (isNullItem(current)) return false;
        }
        return true;
    }

    /**
     * Convert an List of ItemStack to an ItemStack array
     *
     * @param inventory
     * @return
     */
    public static ItemStack[] inventoryAsArray(List<ItemStack> inventory) {
        return inventory.toArray(new ItemStack[0]);
    }

    /**
     * Check if the item was null or air
     *
     * @param item
     * @return
     */
    public static boolean isNullItem(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Check if the block was null or air
     *
     * @param block
     * @return
     */
    public static boolean isNullItem(Block block) {
        return block == null || block.getType() == Material.AIR;
    }

    /**
     * Check if the item is a repairable item
     *
     * @param item
     * @return
     */
    public static boolean isRepairableItem(ItemStack item) {
        return item.getMaxStackSize() == 1 && item.getDurability() >= 1;
    }

    /**
     * Check if the player has space in his inventory
     *
     * @param player
     * @param item
     * @param count
     * @return
     */
    public static boolean hasSpaceInventory(Player player, ItemStack item, int count) {
        int left = count;
        ItemStack[] items = player.getInventory().getContents();
        for (ItemStack stack : items) {
            if (stack == null || stack.getType() == Material.AIR) {
                left -= item.getMaxStackSize();
            } else if (stack.getType() == item.getType() && stack.getData().getData() == item.getData().getData() && item.getMaxStackSize() > 1 && stack.getAmount() < item.getMaxStackSize()) {
                left -= stack.getMaxStackSize() - stack.getAmount();
            }
            if (left <= 0) break;
        }
        return (left <= 0);
    }

    /**
     * Get the nice name of an item stack (DIAMOND_SWORD -> Diamond Sword)
     *
     * @param item
     * @return
     */
    public static String getItemStackName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) return item.getItemMeta().getDisplayName();

        String itemName = "";
        String separator = " ";
        String[] materialName = item.getType().name().replace("_", separator).split(separator);
        for (int length = materialName.length, i = 0; i < length; ++i) {
            itemName += Character.toUpperCase(materialName[i].charAt(0)) + materialName[i].substring(1).toLowerCase();
            if (i < (length - 1)) itemName += separator;
        }

        return itemName;
    }

    /**
     * Add an item to the player inventory or drop is don't have any space
     *
     * @param player
     * @param items
     */
    public static void addItemOrDrop(Player player, List<ItemStack> items) {
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                ItemStack itemStack = item.clone();
                HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(itemStack);
                if (!leftOver.isEmpty()) player.getWorld().dropItem(player.getLocation(), itemStack);
            }
        }
    }

    /**
     * Add an item to the player inventory or drop is don't have any space
     *
     * @param player
     * @param itemStack
     */
    public static void addItemOrDrop(Player player, ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(itemStack);
            if (!leftOver.isEmpty()) player.getWorld().dropItem(player.getLocation(), itemStack);
        }
    }

    /**
     * Drop items at a location
     *
     * @param location
     * @param items
     */
    public static void dropItems(Location location, List<ItemStack> items) {
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                ItemStack itemStack = item.clone();
                location.getWorld().dropItem(location, itemStack);
            }
        }
    }

    /**
     * Drop an item at a location
     *
     * @param location
     * @param itemStack
     */
    public static void dropItem(Location location, ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            location.getWorld().dropItem(location, itemStack);
        }
    }

    /**
     * Add an item to the player inventory
     *
     * @param player
     * @param item
     */
    public static void addItem(Player player, ItemStack item) {
        addItem(player, item, 1);
    }

    /**
     * Add an item to the player inventory
     *
     * @param player
     * @param item
     * @param quantity
     */
    public static void addItem(Player player, ItemStack item, int quantity) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack current = new ItemStack(item);
        int max = current.getMaxStackSize();
        if (quantity > max) {
            int leftOver = quantity;
            while (leftOver > 0) {
                int add = 0;
                add += Math.min(leftOver, max);
                current = current.clone();
                current.setAmount(add);
                playerInventory.addItem(current);
                leftOver -= add;
            }
        } else {
            current = current.clone();
            current.setAmount(quantity);
            playerInventory.addItem(current);
        }
    }

    /**
     * Add items to the player inventory
     *
     * @param player
     * @param items
     */
    public static void addItems(Player player, List<ItemStack> items) {
        for (ItemStack item : items) addItem(player, item, item.getAmount());
    }

    /**
     * Return the quantity of an item in the player inventory
     *
     * @param player
     * @param item
     * @return
     */
    public static int getItemCount(Player player, ItemStack item) {
        int quantityInInventory = 0;
        PlayerInventory playerInventory = player.getInventory();
        for (ItemStack current : playerInventory.getContents()) {
            if (!isNullItem(current) && current.getType() == item.getType() && current.getData().getData() == item.getData().getData())
                quantityInInventory += current.getAmount();
        }
        return quantityInInventory;
    }

    /**
     * Decrement the player current item
     *
     * @param player
     * @param quantity
     */
    public static void decrementCurrentItem(Player player, int quantity) {
        ItemStack item = player.getItemInHand();
        int currentAmount = item.getAmount();

        if (currentAmount <= 1) player.setItemInHand(null);
        else {
            int amount = currentAmount - quantity;
            item.setAmount(amount);
        }
    }

    /**
     * Decrement a specific player item
     *
     * @param player
     * @param item
     * @param quantity
     */
    public static void decrementItem(Player player, ItemStack item, int quantity) {
        int toRemove = quantity;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (toRemove <= 0) break;

            if (itemStack != null && itemStack.getType() == item.getType() && itemStack.getData().getData() == item.getData().getData()) {
                int amount = itemStack.getAmount() - toRemove;
                toRemove -= itemStack.getAmount();

                if (amount <= 0) player.getInventory().removeItem(itemStack);
                else itemStack.setAmount(amount);
            }
        }
    }

    /**
     * Decrement the player current item or the inventory item
     *
     * @param player
     * @param item
     * @param quantity
     */
    public static void decrementCurrentOrInventoryItem(Player player, ItemStack item, int quantity) {
        int toRemove = quantity;

        ItemStack currentItem = player.getInventory().getItemInHand();
        if (currentItem != null && currentItem.getType() == item.getType() && currentItem.getData().getData() == item.getData().getData()) {
            int amount = currentItem.getAmount() - toRemove;
            toRemove -= currentItem.getAmount();

            if (amount <= 0) player.getInventory().setItemInHand(new ItemStack(Material.AIR));
            else {
                currentItem.setAmount(amount);
                player.getInventory().setItemInHand(currentItem);
            }
        }

        if (toRemove <= 0) return;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (toRemove <= 0) break;
            if (itemStack != null && itemStack.getType() == item.getType() && itemStack.getData().getData() == item.getData().getData()) {
                int amount = itemStack.getAmount() - toRemove;
                toRemove -= itemStack.getAmount();

                if (amount <= 0) player.getInventory().removeItem(itemStack);
                else {
                    itemStack.setAmount(amount);
                    player.updateInventory();
                }
            }
        }
    }

    /**
     * Damage the player item
     *
     * @param player
     * @param item
     * @param max
     */
    public static void damageItem(Player player, ItemStack item, int max) {
        player.setItemInHand(changeDurability(player, item, max));
    }

    /**
     * Change an item durability
     *
     * @param player
     * @param item
     * @param max
     * @return
     */
    public static ItemStack changeDurability(Player player, ItemStack item, int max) {
        item.setDurability((short) (item.getDurability() + 1));
        if (item.getDurability() >= max) {
            return new ItemStack(Material.AIR);
        }
        return item;
    }

    /**
     * Get a random loot from a list of ChanceLoot
     *
     * @param loots
     * @return
     */
    public static ItemStack getRandomLoot(List<ChanceLoot> loots) {
        if (loots.isEmpty()) return null;

        double randomValue = Math.random();
        double cumulativeProbability = 0.0D;
        for (ChanceLoot loot : loots) {
            cumulativeProbability += loot.getChance();
            if ((randomValue * 100) <= cumulativeProbability) {
                return loot.getItem();
            }
        }
        return null;
    }

    /**
     * Edit an item with a name and lore
     *
     * @param item
     * @param name
     * @param lore
     * @return
     */
    public static ItemStack editItem(ItemStack item, String name, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
