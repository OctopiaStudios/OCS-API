package fr.octopiastudios.api.utils.menu;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager implements Listener {

    @Getter
    private static MenuManager instance;

    private Plugin plugin;
    private final Map<UUID, GUI> guis;

    public MenuManager(Plugin plugin) {
        instance = this;

        this.plugin = plugin;
        this.guis = Maps.newHashMap();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && event.getInventory().getHolder() instanceof VirtualHolder) {
            ((VirtualHolder) event.getInventory().getHolder()).getGui().onInventoryClick(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player && event.getInventory().getHolder() instanceof VirtualHolder) {
            GUI gui = ((VirtualHolder) event.getInventory().getHolder()).getGui();
            gui.onInventoryClose(event);
            if (gui instanceof VirtualGUI) {
                VirtualGUI virtualGUI = (VirtualGUI) gui;
                this.guis.put(event.getPlayer().getUniqueId(), virtualGUI);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(this.plugin)) {
            this.closeOpenMenus();
            this.plugin = null;
        }
    }

    public void closeOpenMenus() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) player.closeInventory();
    }
}

