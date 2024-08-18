package fr.octopiastudios.api.logs;

import com.google.common.collect.Lists;
import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.OSPlugin;
import fr.octopiastudios.api.modules.objects.Module;
import fr.octopiastudios.api.tasks.TickUtils;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter
public class LogsManager extends Module {

    @Getter
    public static LogsManager instance;
    private final List<String> logs;

    public LogsManager(OSPlugin plugin) {
        super(plugin, "Logs");
        this.setDesactivable(false);
        instance = this;

        this.logs = Lists.newArrayList();

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::writeLogs, 1L, TickUtils.fromMinute(15));
    }

    @Override
    public void onLoad() {
        this.createFiles();
        super.onLoad();
    }

    @Override
    public void onUnload() {
        this.writeLogs();
        super.onUnload();
    }

    public static void sendConsole(String prefix, String message, LogType logType) {
        String finalMessage = prefix.isEmpty() ? message : prefix + " " + message;
        switch (logType) {
            case ERROR:
                LogsManager.getInstance().getPlugin().getLogger().severe(finalMessage);
                break;
            case WARNING:
                LogsManager.getInstance().getPlugin().getLogger().warning(finalMessage);
                break;
            case SUCCESS:
                LogsManager.getInstance().getPlugin().getLogger().info(finalMessage);
                break;
            default:
        }
    }

    public static void log(String message, LogType logType) {
        sendConsole("[OCS-API]", message, logType);
        LogsManager.getInstance().getLogs().add("[" + logType.name() + " / " + new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date()) + "] " + message);
    }

    public static void log(String[] messages, LogType logType) {
        String[] arrayOfString;
        int j = (arrayOfString = messages).length;
        for (int i = 0; i < j; i++) {
            String message = arrayOfString[i];
            log(message, logType);
        }
    }

    public File getCurrentLogFile() {
        SimpleDateFormat frenchDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        Date date = new Date();
        return new File(OSAPI.getAPI().getDataFolder(), "/logs/" + frenchDateFormat.format(date) + ".txt");
    }

    public void createFiles() {
        /**
         * Create the logs directory if not exist (only first time)
         */
        File directory = new File(OSAPI.getAPI().getDataFolder(), "logs/");
        if (!directory.exists()) {
            try {
                directory.mkdir();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * Create the current log file if not exist
         */
        File file = this.getCurrentLogFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void writeLogs() {
        if (this.logs.isEmpty()) return;

        this.createFiles(); // Create the file if not exist
        File file = this.getCurrentLogFile();
        for (String log : this.logs) {
            try {
                log = log + "\n";
                Files.write(file.toPath(), log.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        this.logs.clear();
    }
}
