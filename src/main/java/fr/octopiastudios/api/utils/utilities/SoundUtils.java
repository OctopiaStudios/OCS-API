package fr.octopiastudios.api.utils.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SoundUtils {

    public static Sound parseSound(String sound) {
        sound = sound.toUpperCase();
        Sound s = null;
        try {
            s = Sound.valueOf(sound);
            return s;
        } catch (Exception exception) {
            if (s == null) {
                String str;
                switch ((str = sound).hashCode()) {
                    case -1701085205:
                        if (!str.equals("VILLAGER_HAGGLE"))
                            break;
                        sound = "ENTITY_VILLAGER_TRADING";
                        break;
                    case -1348968106:
                        if (!str.equals("LEVEL_UP"))
                            break;
                        sound = "ENTITY_PLAYER_LEVELUP";
                        break;
                    case -692386820:
                        if (!str.equals("ORB_PICKUP"))
                            break;
                        sound = "ENTITY_EXPERIENCE_ORB_PICKUP";
                        break;
                    case -591166271:
                        if (!str.equals("EXPLODE"))
                            break;
                        sound = "ENTITY_GENERIC_EXPLODE";
                        break;
                    case 64212328:
                        if (!str.equals("CLICK"))
                            break;
                        sound = "UI_BUTTON_CLICK";
                        break;
                    case 203096142:
                        if (!str.equals("SUCCESSFUL_HIT"))
                            break;
                        sound = "ENTITY_ARROW_HIT_PLAYER";
                        break;
                    case 460854522:
                        if (!str.equals("VILLAGER_HIT"))
                            break;
                        sound = "ENTITY_VILLAGER_HURT";
                        break;
                    case 1401613101:
                        if (!str.equals("VILLAGER_IDLE"))
                            break;
                        sound = "ENTITY_VILLAGER_AMBIENT";
                        break;
                }
                try {
                    s = Sound.valueOf(sound);
                    if (s != null)
                        return s;
                } catch (Exception exception1) {
                }
                try {
                    s = Sound.valueOf("ENTITY_" + sound);
                    if (s != null)
                        return s;
                } catch (Exception exception1) {
                }
                try {
                    s = Sound.valueOf("BLOCK_" + sound);
                    if (s != null)
                        return s;
                } catch (Exception exception1) {
                }
            }
            return s;
        }
    }

    public static void sendSound(Player player, String sound) {
        Sound s = parseSound(sound);
        if (sound != null && player != null && player.isOnline())
            sendSound(player, s, 1.0F, 1.0F);
    }

    public static void sendSound(Player player, Sound sound) {
        if (sound != null && player != null && player.isOnline())
            sendSound(player, sound, 1.0F, 1.0F);
    }

    public static void sendSound(ArrayList<Player> players, String sound) {
        Sound s = parseSound(sound);
        for (Player p : players) {
            if (s != null && p != null && p.isOnline())
                sendSound(p, s, 1.0F, 1.0F);
        }
    }

    public static void sendSound(ArrayList<Player> players, Sound sound) {
        for (Player p : players) {
            if (sound != null && p != null && p.isOnline())
                sendSound(p, sound, 1.0F, 1.0F);
        }
    }

    public static void sendSound(Player player, Sound sound, float volume, float pitch) {
        if (sound != null && player != null && player.isOnline())
            player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void sendSound(Player player, Location location, Sound sound) {
        location.getWorld().playSound(location, sound, 1.0F, 1.0F);
    }

    public static void sendSound(Player player, Location location, Sound sound, float volume, float pitch) {
        player.playSound(location, sound, volume, pitch);
    }

    public static void sendSoundForAll(Sound sound) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) sendSound(player, sound);
    }

    public static void sendSoundForAll(String sound) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) sendSound(player, sound);
    }

    public static void sendSoundForAll(Sound sound, float volume, float pitch) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) sendSound(player, sound, volume, pitch);
    }

    public static void sendSoundAt(Location location, Sound sound) {
        location.getWorld().playSound(location, sound, 1.0F, 1.0F);
    }

    public static void sendSoundAt(Location to, String sound) {
        Sound s = parseSound(sound);
        if (s != null && to != null)
            to.getWorld().playSound(to, s, 1.0F, 1.0F);
    }

    public static void sendSoundAt(Location location, Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }
}
