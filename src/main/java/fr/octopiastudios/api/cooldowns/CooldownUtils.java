package fr.octopiastudios.api.cooldowns;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownUtils {

    private static final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public static void clearCooldowns() {
        cooldowns.clear();
    }

    public static void createCooldown(String key) {
        if (cooldowns.containsKey(key)) {
            throw new IllegalArgumentException("This cooldown already exists!");
        }
        cooldowns.put(key, new HashMap<>());
    }

    public static Map<UUID, Long> getCooldownMap(String key) {
        return cooldowns.get(key);
    }

    public static void addCooldown(String key, Player player, int seconds) {
        cooldowns.computeIfAbsent(key, k -> new HashMap<>());
        long expiryTime = System.currentTimeMillis() + seconds * 1000L;
        cooldowns.get(key).put(player.getUniqueId(), expiryTime);
    }

    public static boolean isOnCooldown(String key, Player player) {
        if (!cooldowns.containsKey(key)) {
            return false;
        }
        Long expiryTime = cooldowns.get(key).get(player.getUniqueId());
        return expiryTime != null && System.currentTimeMillis() <= expiryTime;
    }

    public static int getCooldownForPlayerInt(String key, Player player) {
        Long remainingTime = getRemainingCooldown(key, player);
        return remainingTime != null ? (int) (remainingTime / 1000L) : 0;
    }

    public static long getCooldownForPlayerLong(String key, Player player) {
        Long remainingTime = getRemainingCooldown(key, player);
        return remainingTime != null ? remainingTime : 0;
    }

    private static Long getRemainingCooldown(String key, Player player) {
        if (!cooldowns.containsKey(key)) {
            return null;
        }
        Long expiryTime = cooldowns.get(key).get(player.getUniqueId());
        if (expiryTime == null) {
            return null;
        }
        long remainingTime = expiryTime - System.currentTimeMillis();
        return remainingTime > 0 ? remainingTime : null;
    }

    public static void removeCooldown(String key, Player player) {
        if (!cooldowns.containsKey(key)) {
            throw new IllegalArgumentException(key + " doesn't exist!");
        }
        cooldowns.get(key).remove(player.getUniqueId());
    }
}
