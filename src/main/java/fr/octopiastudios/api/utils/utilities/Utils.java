package fr.octopiastudios.api.utils.utilities;

import com.google.common.collect.Lists;
import fr.octopiastudios.api.OSPlugin;
import fr.octopiastudios.api.cooldowns.CooldownUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Utils {

    public static String SIMPLE_LINE = StringUtils.repeat("-", 53);
    public static String GREY_LINE = "§7§m" + SIMPLE_LINE;
    public static String GOLD_LINE = "§6§m" + SIMPLE_LINE;
    public static String SCOREBOARD_LINE = "§7§m" + StringUtils.repeat("-", 20);

    private static final Pattern formattingCodePattern = Pattern.compile("(?i)" + '&' + "[0-9A-FK-OR]");
    private static final Pattern mcFormattingCodePattern = Pattern.compile("(?i)" + '\u00a7' + "[0-9A-FK-OR]");
    private static final Pattern rgbCodePattern = Pattern.compile("(?i)¡(\\\\d{1,3}),(\\\\d{1,3}),(\\\\d{1,3})¡");

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String toStringOnOff(boolean value) {
        return value ? ("§aON") : ("§cOFF");
    }

    public static Player[] getOnlinePlayers() {
        List<Player> players = Lists.newArrayList(Bukkit.getServer().getOnlinePlayers());
        return players.toArray(new Player[0]);
    }

    public static String formatTime(long duration) {
        int secondsLeft = (int) (duration / 1000L);
        return secondsLeft <= 60 ? DurationFormatUtils.formatDuration(duration, secondsLeft > 10 ? "ss" : "s") + "s" : DurationFormatUtils.formatDuration(duration, "mm:ss");
    }

    public static String formatDelay(long delay, boolean isEnglish) {
        // Convertir le délai en jours, heures, minutes et secondes
        long days = delay / 86400000; // 1 jour = 86400000 millisecondes
        long hours = (delay % 86400000) / 3600000; // 1 heure = 3600000 millisecondes
        long minutes = (delay % 3600000) / 60000; // 1 minute = 60000 millisecondes
        long seconds = (delay % 60000) / 1000; // 1 seconde = 1000 millisecondes

        // Construire la chaîne de caractères formatée en fonction des valeurs
        StringBuilder formattedString = new StringBuilder();

        String dayValue = isEnglish ? " day" : " jour";
        String hourValue = isEnglish ? " hour" : " heure";
        String secondValue = isEnglish ? " second" : " seconde";

        if (days > 0) {
            formattedString.append(days).append(dayValue);
            if (days > 1) {
                formattedString.append("s");
            }
            if (hours > 0 || minutes > 0 || seconds > 0) {
                formattedString.append(" ");
            }
        }
        if (hours > 0) {
            formattedString.append(hours).append(hourValue);
            if (hours > 1) {
                formattedString.append("s");
            }
            if (minutes > 0 || seconds > 0) {
                formattedString.append(" ");
            }
        }
        if (minutes > 0) {
            formattedString.append(minutes).append(" minute");
            if (minutes > 1) {
                formattedString.append("s");
            }
            if (seconds > 0) {
                formattedString.append(" ");
            }
        }
        if (seconds > 0) {
            formattedString.append(seconds).append(secondValue);
            if (seconds > 1) {
                formattedString.append("s");
            }
        }

        return formattedString.toString();
    }

    public static String format(String message, Object... args) {
        return color(String.format(message, args));
    }

    public static String getTitle(String title) {
        int titleLength = title.length();
        int remaining = 52 - titleLength;
        String line = "§6§m" + StringUtils.repeat("-", remaining);
        return "§6§m-§6" + title + line;
    }

    public static boolean hasPermission(Player player, List<String> permissions) {
        return permissions.stream().anyMatch(player::hasPermission);
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '\u00A7';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String getTextWithoutFormattingCodes(String string) {
        string = translateAlternateColorCodes('&', string);
        string = formattingCodePattern.matcher(string).replaceAll("");
        string = rgbCodePattern.matcher(string).replaceAll("");
        return mcFormattingCodePattern.matcher(string).replaceAll("");
    }

    public static boolean isSameWorld(World playerWorld, World targetWorld) {
        return playerWorld.getName().equals(targetWorld.getName());
    }

    public static File getFormatedFile(String fileName, OSPlugin plugin) {
        return new File(plugin.getDataFolder(), fileName);
    }

    public static void deleteFile(File file) {
        if (!file.exists()) return;
        file.delete();
    }

    public void createDirectory(String directory, OSPlugin plugin) {
        File file = this.getFormatedFile(directory, plugin);
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void strikeLightningWithoutFire(Location locaction) {
        locaction.getWorld().strikeLightningEffect(locaction);
        for (LivingEntity e : locaction.getWorld().getLivingEntities()) {
            if (e.getLocation().distance(locaction) < 3D) {
                e.damage(2); //one heart
            }
        }
    }

    public static void knockbackPlayer(Player player) {
        player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(-1));
        CooldownUtils.addCooldown("knockbackFall", player, 5);
    }

    public static void knockbackPlayer(Player player, int knockback) {
        player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(-knockback));
        CooldownUtils.addCooldown("knockbackFall", player, 5);
    }

    public static void broadcastMessage(String... messages) {
        int count = 0;
        while (count < messages.length) {
            String message = messages[count];
            Bukkit.broadcastMessage(Utils.color(message));
            ++count;
        }
    }

    public static void broadcastMessage(List<Player> players, String... messages) {
        int count = 0;
        while (count < messages.length) {
            String message = messages[count];
            for (Player player : players) player.sendMessage(Utils.color(message));
            ++count;
        }
    }

    public static void broadcastMessage(Player player, String... messages) {
        int count = 0;
        while (count < messages.length) {
            String message = messages[count];
            player.sendMessage(Utils.color(message));
            ++count;
        }
    }

    public static void broadcastLineMessage(String... messages) {
        broadcastMessage(messages);
    }

    public static void broadcastLineMessage(Player player, String... messages) {
        broadcastMessage(player, messages);
    }

    public static Location getLocationString(String s) {
        if (s == null || s.trim().isEmpty()) return null;

        String[] parts = s.split(",");
        if (parts.length == 6) {
            World w = Bukkit.getServer().getWorld(parts[0].replace("Location{world=CraftWorld{name=", "").replace("}", ""));
            double x = Double.parseDouble(parts[1].replace("x=", ""));
            double y = Double.parseDouble(parts[2].replace("y=", ""));
            double z = Double.parseDouble(parts[3].replace("z=", ""));
            float pitch = Float.parseFloat(parts[4].replace("pitch=", ""));
            float yaw = Float.parseFloat(parts[5].replace("yaw=", "").replace("}", ""));
            return new Location(w, x, y, z, pitch, yaw);
        }
        return null;
    }

    private static long convert(int value, char unit) {
        switch (unit) {
            case 'y':
                return value * TimeUnit.DAYS.toMillis(365L);
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'd':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'h':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            case 's':
                return value * TimeUnit.SECONDS.toMillis(1L);
        }
        return -1L;
    }

    public static boolean isInteger(String string) {
        boolean isInteger = true;
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            isInteger = false;
        }
        return isInteger;
    }

    public static int randomInt(int min, int max) {
        Random random = new Random();
        int range = max - min + 1;
        return random.nextInt(range) + min;
    }

    public static int countChar(String message, String target) {
        int count = 0;
        int index = message.indexOf(target);
        while (index != -1) {
            count++;
            index = message.indexOf(target, index + 1);
        }
        return count;
    }

    public static String generateString(Random random, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(random.nextInt(characters.length()));
        }
        return new String(text);
    }

    public static String formatLocation(Location location) {
        return color("&6X&7: &e" + location.getBlockX() + " &6Y&7: &e" + location.getBlockY() + " &6Z&7: &e" + location.getBlockZ());
    }

    /**
     * Check if a class is loaded
     *
     * @param classPath
     * @return
     */
    public static boolean isClassLoaded(String classPath) {
        if (classPath == null || classPath.isEmpty()) return false;
        try {
            Class.forName(classPath);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Check if a plugin is enabled
     *
     * @param pluginName
     * @return
     */
    public static boolean isEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    /**
     * Get a plugin (cast to specific plugin)
     *
     * @param pluginName
     * @return
     */
    public static Plugin getPlugin(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }

    /**
     * Get web content from a URL
     *
     * @param url
     * @return
     */
    public static String getContent(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    /**
     * Convert a plugin version to an integer
     */
    public static Integer convertVersion(String v) {
        v = v.replaceAll("[^\\d.]", "");
        int version = 0;
        if (v.contains(".")) {
            StringBuilder lVersion = new StringBuilder();
            for (String s : v.split("\\.")) {
                if (s.length() == 1) {
                    s = "0" + s;
                }
                lVersion.append(s);
            }

            if (Utils.isInteger(lVersion.toString())) {
                version = Integer.parseInt(lVersion.toString());
            }
        } else {
            if (Utils.isInteger(v)) {
                version = Integer.parseInt(v);
            }
        }
        return version;
    }
}
