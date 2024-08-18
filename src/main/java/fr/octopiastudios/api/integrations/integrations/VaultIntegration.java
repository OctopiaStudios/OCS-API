package fr.octopiastudios.api.integrations.integrations;

import fr.octopiastudios.api.integrations.Integration;
import fr.octopiastudios.api.logs.LogType;
import fr.octopiastudios.api.logs.LogsManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultIntegration extends Integration {

    private Chat chat;
    private Economy economy;
    private Permission permission;

    public VaultIntegration() {
        super("Vault", "net.milkbowl.vault.economy.Economy");
    }

    @Override
    public void onLoad() {
        this.setupChat();
        this.setupEconomy();
        this.setupPermissions();
    }

    @Override
    public void onUnload() {

    }

    // Getters
    public Permission getPermission() {
        if (permission == null) this.setupPermissions();
        return permission;
    }

    public Economy getEconomy() {
        if (economy == null) this.setupEconomy();
        return economy;
    }

    public Chat getChat() {
        if (chat == null) this.setupChat();
        return chat;
    }

    /**
     * Setup the chat integration
     */
    private void setupChat() {
        if (!this.isEnabled()) return;

        try {
            RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
            if (rsp == null) return;
            chat = rsp.getProvider();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load the chat integration of Vault.");
        }
    }

    private void setupEconomy() {
        if (!this.isEnabled()) return;

        try {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) return;
            economy = rsp.getProvider();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load the economy integration of Vault.");
        }
    }

    private void setupPermissions() {
        if (!this.isEnabled()) return;

        try {
            RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp == null) return;
            permission = rsp.getProvider();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load the permission integration of Vault.");
        }
    }

    /**
     * Economy methods
     */
    public double getBalance(String user) {
        Economy economy = this.getEconomy();
        if (economy == null) {
            LogsManager.sendConsole("", "Vault integration is not enabled. Please, make sure the plugin is on you're server.", LogType.ERROR);
            return 0.0D;
        }
        return economy.getBalance(user);
    }

    public double getBalance(Player player) {
        Economy economy = this.getEconomy();
        if (economy == null) {
            LogsManager.sendConsole("", "Vault integration is not enabled. Please, make sure the plugin is on you're server.", LogType.ERROR);
            return 0.0D;
        }
        return economy.getBalance(player);
    }

    public boolean setBalance(Player player, double value) {
        Economy economy = this.getEconomy();
        if (economy == null) {
            LogsManager.sendConsole("", "Vault integration is not enabled. Please, make sure the plugin is on you're server.", LogType.ERROR);
            return false;
        }

        economy.withdrawPlayer(player, value);
        economy.depositPlayer(player, value);
        return true;
    }

    public boolean has(Player player, double value) {
        Economy economy = this.getEconomy();
        if (economy == null) {
            LogsManager.sendConsole("", "Vault integration is not enabled. Please, make sure the plugin is on you're server.", LogType.ERROR);
            return false;
        }
        return economy.has(player, value);
    }

    public boolean depositMoney(Player player, double value) {
        Economy economy = this.getEconomy();
        if (economy == null) {
            LogsManager.sendConsole("", "Vault integration is not enabled. Please, make sure the plugin is on you're server.", LogType.ERROR);
            return false;
        }
        economy.depositPlayer(player, value);
        return true;
    }

    public boolean depositMoney(String player, double value) {
        Economy economy = this.getEconomy();
        if (economy == null) {
            LogsManager.sendConsole("", "Vault integration is not enabled. Please, make sure the plugin is on you're server.", LogType.ERROR);
            return false;
        }
        economy.depositPlayer(player, value);
        return true;
    }

    public boolean withdrawMoney(Player player, double value) {
        Economy economy = this.getEconomy();
        if (economy == null) {
            LogsManager.sendConsole("", "Vault integration is not enabled. Please, make sure the plugin is on you're server.", LogType.ERROR);
            return false;
        }
        economy.withdrawPlayer(player, value);
        return true;
    }
}
