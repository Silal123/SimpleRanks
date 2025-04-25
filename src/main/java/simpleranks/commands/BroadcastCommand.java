package simpleranks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simpleranks.utils.PlayerRank;
import simpleranks.utils.Prefix;
import simpleranks.utils.config.PlayerConfiguration;

import java.util.Arrays;
import java.util.List;

public class BroadcastCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Prefix.SYSTEM.err() + "Please specify a §crank§7 you want to broadcast to!");
            return true;
        }
        String r = args[0];

        if (!PlayerRank.isRankExistent(r)) {
            sender.sendMessage(Prefix.SYSTEM.err() + "The specified rank §cdoes not exist§7!");
            return true;
        }
        PlayerRank rank = PlayerRank.get(r);

        if (args.length < 2) {
            sender.sendMessage(Prefix.SYSTEM.err() + "Please add a §cmessage§7 you want to broadcast!");
            return true;
        }
        String m = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : null;

        String message = null;
        if (sender instanceof Player p) message = "§8[§6BROADCAST§8]§7 " + PlayerConfiguration.getFor(p).getRank().color() + p.getName() + "§8:§7 " + m;
        else if (sender instanceof ConsoleCommandSender c) message = "§8[§6BROADCAST§8]§7 §cServer§8:§7 " + m;
        else return true;

        final String fm = message;
        Bukkit.getOnlinePlayers().stream().filter(player -> PlayerConfiguration.getFor(player).getRank().id() == rank.id()).forEach(player -> { player.sendMessage(""); player.sendMessage(fm); player.sendMessage(""); });
        sender.sendMessage(Prefix.SYSTEM.def() + "Broadcast was §asuccessfuly§7 sent!");
        return true;
    }
}
