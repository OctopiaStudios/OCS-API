package fr.octopiastudios.api.product;

import fr.octopiastudios.api.OSPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ProductInformations {

    private final OSPlugin plugin;
    private final long timeAtStart;

    public ProductInformations(OSPlugin plugin) {
        this.plugin = plugin;
        this.timeAtStart = System.currentTimeMillis();
    }

    public void sendInformations() {
        this.log("&7==========================================================================");
        this.log("&fProject: &d" + this.plugin.getPluginName() + " &f- Version " + this.plugin.getDescription().getVersion());
        this.log("&fDeveloper: &eOctopia Studios (@haizen - https://github.com/OctopiaStudios)");
        this.log("&fSupport: &ahttps://studios.octopiagames.fr/");
        this.log("&fServeur: &c" + this.plugin.getServer().getVersion() + " version.");
        this.log("&aPlugin enabled (" + (System.currentTimeMillis() - timeAtStart) + " ms).");
        this.log("&7==========================================================================");
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
