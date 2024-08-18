package fr.octopiastudios.api.utils.menu;

import com.google.common.collect.Maps;
import fr.octopiastudios.api.utils.menu.actions.CloseAction;
import fr.octopiastudios.api.utils.menu.items.BackItem;
import fr.octopiastudios.api.utils.menu.items.UpdaterItem;
import fr.octopiastudios.api.utils.menu.items.VirtualItem;
import fr.octopiastudios.api.utils.utilities.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;

@Getter
@Setter
public class VirtualGUI implements GUI {

    private String name;
    private Size size;
    private Map<Integer, VirtualItem[]> pages;
    private CloseAction closeAction;
    private boolean allowBackItem, allowClick;
    private VirtualItem[] currentPageItems;
    private int currentPage = 1;
    private boolean firstOpen = true;

    public VirtualGUI(String name, Size size) {
        this.name = Utils.color(name);
        this.size = size;
        this.pages = Maps.newHashMap();
        this.growInventory(this.currentPage, true);
    }

    public VirtualGUI allowBackItem() {
        allowBackItem = true;
        return this;
    }

    public VirtualGUI allowClick() {
        allowClick = true;
        return this;
    }

    public VirtualGUI onCloseAction(CloseAction closeAction) {
        this.closeAction = closeAction;
        return this;
    }

    public VirtualGUI setItem(int position, VirtualItem menuItem) {
        this.pages.get(this.currentPage)[position] = menuItem;
        return this;
    }

    public VirtualGUI addItem(VirtualItem item) {
        int next = getNextEmptySlot(this.currentPage);
        int inventorySize = this.getSize().getSize();
        if (next == -1 || next == (inventorySize - 1)) {
            this.currentPage += 1;
            this.growInventory(this.currentPage, true);
            next = this.getNextEmptySlot(this.currentPage);
        }
        this.setItem(next, item);
        return this;
    }

    private int getNextEmptySlot(int page) {
        VirtualItem[] items = this.pages.get(page);
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public VirtualGUI fillEmptySlots(VirtualItem menuItem) {
        for (int i = 0; i < currentPageItems.length; i++) {
            if ((!allowBackItem) || (i != size.getSize() - 1)) {
                if (currentPageItems[i] == null)
                    currentPageItems[i] = menuItem;
            }
        }
        return this;
    }

    public void growInventory(int page, boolean update) {
        this.pages.put(page, new VirtualItem[this.size.getSize()]);
        if (update) {
            this.updatePage(page);
        }
    }

    public void updatePage(int page) {
        this.currentPageItems = this.pages.get(page);
    }

    public void open(Player player) {
        if (this.firstOpen) {
            this.firstOpen = false;
            this.currentPage = 1;
        }

        Inventory inventory = Bukkit.createInventory(new VirtualHolder(this, Bukkit.createInventory(player, size.getSize())), size.getSize(), name);
        this.apply(inventory, player);
        player.openInventory(inventory);
    }

    public void apply(Inventory inventory, Player player) {
        if (this.allowBackItem && currentPageItems[inventory.getSize() - 1] == null) {
            currentPageItems[inventory.getSize() - 1] = (VirtualItem) new BackItem();
        }
        for (int i = 0; i < currentPageItems.length; i++) {
            VirtualItem item = currentPageItems[i];
            if (item != null) {
                inventory.setItem(i, item.getItem());
                if (item instanceof UpdaterItem) {
                    UpdaterItem updater = (UpdaterItem) item;
                    updater.startUpdate(player, i);
                }
            }
        }
    }

    public void onInventoryClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        boolean cancelled = false;
        if (!this.allowClick) {
            cancelled = true;
        }
        if (slot >= 0 && slot < this.size.getSize() && currentPageItems[slot] != null) {
            VirtualItem item = currentPageItems[slot];
            if (!item.isAllowClick()) {
                cancelled = true;
            } else if (cancelled && item.isAllowClick()) {
                cancelled = false;
            }
            if (item.getAction() != null)
                item.getAction().onClick(event);
            if (event.isCancelled()) {
                cancelled = true;
            }
        }
        event.setCancelled(cancelled);
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        if (closeAction != null) {
            closeAction.onClose(event);
            closeAction = null;
        }

        for (int i = 0; i < currentPageItems.length; i++) {
            VirtualItem item = currentPageItems[i];
            if ((item != null) && ((item instanceof UpdaterItem))) {
                UpdaterItem updater = (UpdaterItem) item;
                updater.cancelUpdate();
            }
        }
    }

    public void destroy() {
        name = null;
        size = null;
        currentPageItems = null;
        pages = null;
    }


}
