package fr.octopiastudios.api.utils.menu.items;

import fr.octopiastudios.api.utils.menu.MenuManager;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class UpdaterItem extends VirtualItem {

    private int delay;
    private int taskId = -1;
    private UpdateAction updateAction;

    public UpdaterItem(ItemStack item, int delay) {
        super(item);
        this.delay = delay;
    }

    public UpdaterItem setUpdateAction(UpdateAction updateAction) {
        this.updateAction = updateAction;
        return this;
    }

    public void startUpdate(final Player player, final int slot) {
        if (this.updateAction == null) {
            return;
        }
        Validate.notNull(MenuManager.getInstance(), "Instance Null");
        Validate.notNull(MenuManager.getInstance().getPlugin(), "Plugin Null");
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MenuManager.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run() {
                Inventory inv = player.getOpenInventory().getTopInventory();
                inv.setItem(slot, UpdaterItem.this.updateAction.action(player, UpdaterItem.this.getItem()));
            }
        }, 1, this.delay);
    }

    public void cancelUpdate() {
        if (this.taskId == -1) return;
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

    public static abstract class UpdateAction {
        public abstract ItemStack action(Player player, ItemStack item);
    }
}

