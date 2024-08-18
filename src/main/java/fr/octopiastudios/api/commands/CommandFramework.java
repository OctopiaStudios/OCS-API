package fr.octopiastudios.api.commands;

import fr.octopiastudios.api.OSAPI;
import fr.octopiastudios.api.commands.annotations.Command;
import fr.octopiastudios.api.commands.annotations.Completer;
import fr.octopiastudios.api.config.APIConfig;
import fr.octopiastudios.api.utils.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandFramework implements CommandExecutor {

    private final Map<String, Map.Entry<Method, Object>> customCommands = new HashMap<>();
    private CommandMap commandMap;
    private final Plugin plugin;

    private final APIConfig config;

    public CommandFramework(Plugin plugin) {
        this.plugin = plugin;
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        } catch (Exception ignored) {
        }

        this.config = OSAPI.getAPI().getApiConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        return handleCommand(sender, cmd, label, args);
    }

    public boolean handleCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        for (int i = args.length; i >= 0; i--) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(label.toLowerCase());
            for (int x = 0; x < i; x++) {
                buffer.append(".").append(args[x].toLowerCase());
            }
            String cmdLabel = buffer.toString();
            if (customCommands.containsKey(cmdLabel)) {
                Method method = customCommands.get(cmdLabel).getKey();
                Object methodObject = customCommands.get(cmdLabel).getValue();
                Command command = method.getAnnotation(Command.class);
                boolean hasPerm = true;

                if ((!command.isConsole()) && (!(sender instanceof Player))) {
                    sender.sendMessage(Utils.color(this.config.onlyInGameCommand));
                    return true;
                }

                if (!command.permissionNode().isEmpty()) {
                    if ((command.permissionNode().equalsIgnoreCase("op")) && (!sender.isOp())) {
                        hasPerm = false;
                    } else if (!sender.hasPermission(command.permissionNode())) {
                        hasPerm = false;
                    }
                }

                if (!hasPerm) {
                    sender.sendMessage(Utils.color(this.config.noPermissionCommand));
                    return true;
                }

                CommandArgs commandArgs = new CommandArgs(sender, cmd, label, args, cmdLabel.split("\\.").length - 1);
                try {
                    method.invoke(methodObject, commandArgs);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        defaultCommand(new CommandArgs(sender, cmd, label, args, 0));
        return true;
    }

    public void registerCommands(Object obj) {
        for (Method m : obj.getClass().getMethods()) {
            if (m.getAnnotation(Command.class) != null) {
                Command command = m.getAnnotation(Command.class);
                if ((m.getParameterTypes().length > 1) || (m.getParameterTypes()[0] != CommandArgs.class)) {
                    System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
                } else {
                    for (String alias : command.name()) registerCommand(alias, m, obj);
                }
            } else if (m.getAnnotation(Completer.class) != null) {
                Completer comp = m.getAnnotation(Completer.class);
                if (m.getParameterTypes().length != 1 || (m.getParameterTypes()[0] != CommandArgs.class)) {
                    System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
                } else if (m.getReturnType() != List.class) {
                    System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                } else {
                    for (String alias : comp.name()) registerCompleter(alias, m, obj);
                }
            }
        }
    }

    public void registerCommand(String label, Method m, Object obj) {
        customCommands.put(label.toLowerCase(), new AbstractMap.SimpleEntry<>(m, obj));
        customCommands.put(this.plugin.getName() + ':' + label.toLowerCase(), new AbstractMap.SimpleEntry<>(m, obj));
        String cmdLabel = label.split("\\.")[0].toLowerCase();

        if (commandMap.getCommand(cmdLabel) == null) {
            org.bukkit.command.Command cmd = new BukkitCommand(cmdLabel, this, plugin);
            commandMap.register(plugin.getName(), cmd);
        }
    }

    public void registerCompleter(String label, Method m, Object obj) {
        String cmdLabel = label.split("\\.")[0].toLowerCase();
        if (commandMap.getCommand(cmdLabel) == null) {
            org.bukkit.command.Command command = new BukkitCommand(cmdLabel, this, plugin);
            commandMap.register(plugin.getName(), command);
        }

        if (commandMap.getCommand(cmdLabel) instanceof BukkitCommand) {
            BukkitCommand command = (BukkitCommand) commandMap.getCommand(cmdLabel);
            if (command != null) {
                if (command.completer == null) {
                    command.completer = new BukkitCompleter();
                }
                command.completer.addCompleter(label, m, obj);
            }
        } else if (commandMap.getCommand(cmdLabel) instanceof PluginCommand) {
            try {
                Object command = commandMap.getCommand(cmdLabel);
                Field field = command.getClass().getDeclaredField("completer");
                field.setAccessible(true);
                if (field.get(command) == null) {
                    BukkitCompleter completer = new BukkitCompleter();
                    completer.addCompleter(label, m, obj);
                    field.set(command, completer);
                } else if (field.get(command) instanceof BukkitCompleter) {
                    BukkitCompleter completer = (BukkitCompleter) field.get(command);
                    if (completer != null) completer.addCompleter(label, m, obj);
                } else {
                    System.out.println("Unable to register tab completer " + m.getName() + ". A tab completer is already registered for that command!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void defaultCommand(CommandArgs args) {
        args.getSender().sendMessage(Utils.color(this.config.commandNotFound));
    }
}
