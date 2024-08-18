package fr.octopiastudios.api.commands;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
public class CommandArgs {

    private final CommandSender sender;
    private final Command command;
    private final String label;
    private final String[] args;

    protected CommandArgs(CommandSender sender, Command command, String label, String[] args, int subCommand) {
        String[] modArgs = new String[args.length - subCommand];
        if (args.length - subCommand >= 0) System.arraycopy(args, subCommand, modArgs, 0, args.length - subCommand);

        StringBuilder buffer = new StringBuilder();
        buffer.append(label);
        for (int x = 0; x < subCommand; x++) buffer.append(".").append(args[x]);
        String cmdLabel = buffer.toString();
        this.sender = sender;
        this.command = command;
        this.label = cmdLabel;
        this.args = modArgs;
    }

    public String getArgs(int index) {
        return args[index];
    }

    public int length() {
        return args.length;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player getPlayer() {
        if ((sender instanceof Player)) return (Player) sender;
        return null;
    }

    public Player asPlayer(int index) {
        return Bukkit.getPlayer(getArgs(index));
    }

    @SuppressWarnings("deprecation")
    public org.bukkit.OfflinePlayer asOfflinePlayer(int index) {
        return Bukkit.getOfflinePlayer(getArgs(index));
    }

    public Integer asInteger(int index) {
        return Ints.tryParse(getArgs(index));
    }

    public Double asDouble(int index) {
        return Doubles.tryParse(getArgs(index));
    }

    public String asString(int start, int end) {
        return StringUtils.join(args, ' ', start, end);
    }
}
