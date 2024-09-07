package simpleranks.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simpleranks.system.ScoreboardSystem;
import simpleranks.utils.*;
import simpleranks.utils.config.DefaultConfiguration;
import simpleranks.utils.config.PlayerConfiguration;

public class RankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender.hasPermission(Permissions.GET_RANK.perm()) && commandSender.hasPermission(Permissions.SET_RANK.perm()))) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this command!"); return true; }
        if (strings.length < 1) { sendHelp(commandSender); return true; }
        String player = strings[0];
        OfflinePlayer updateP = Bukkit.getOfflinePlayer(player);
        if (updateP == null) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The player you specified §cdoes not exist§7!"); return true; }

        if (strings.length < 2) { sendHelp(commandSender); return true; }
        String option = strings[1];

        if (option.equals("set")) {
            if (!commandSender.hasPermission(Permissions.SET_RANK.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to update the rank!"); return true; }
            if (strings.length < 3) { sendHelp(commandSender); return true; }
            String rankName = strings[2];
            if (!PlayerRank.isRankExistent(rankName)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The rank you specified does §cnot exist§7!"); return true; }
            PlayerRank rank = PlayerRank.get(rankName);

            PlayerConfiguration conf = PlayerConfiguration.getFor(updateP.getUniqueId());
            if (!commandSender.hasPermission(Permissions.SET_RANK_ALL.perm())) {
                if (commandSender instanceof Player p) {
                    PlayerRank setterrank = PlayerConfiguration.getFor(p).getRank();
                    if (setterrank.position() > rank.position()) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are not allowed to assign a §chigher rank§7!"); return true; }
                    if (conf.getRank().position() < setterrank.position()) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to update the rank of a higher player!"); return true; }
                }
            }

            if (DefaultConfiguration.rankTimerEnabled.get()) {
                if (strings.length < 4) { sendHelp(commandSender); return true; }
                String timer = strings[3];
                if (timer.equals("infinite")) { timer = "-1"; }
                if (!JavaTools.isInteger(timer)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter a §cnumber§7 as the time!"); return true; }
                int ti = Integer.valueOf(timer);
                if (ti < -1) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter a §cvalid number§7 as time!"); return true; }
                if (ti == -1) { conf.setRankTimer(-1); } else { conf.setRankTimer(ti * 1440); }
                if (ti == -1) { timer = "infinite"; }
                commandSender.sendMessage(Prefix.SYSTEM.def() + "You gave the player §a" + updateP.getName() + "§7 the rank " + rank.color() + rank.displayName() + "§7 for §a" + timer + " days§7!");
            } else {
                conf.setRankTimer(-1);
            }

            conf.setRank(rank);
            ScoreboardSystem.reloadAll();
            PermissionsManager.reload();

            if (!DefaultConfiguration.rankTimerEnabled.get()) {
                commandSender.sendMessage(Prefix.SYSTEM.def() + "You gave the player §a" + updateP.getName() + "§7 the rank " + rank.color() + rank.displayName() + "§7!");
            }
            return true;
        }

        if (option.equals("get")) {
            if (!commandSender.hasPermission(Permissions.GET_RANK.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to retrieve the rank!"); return true; }
            PlayerConfiguration conf = PlayerConfiguration.getFor(updateP.getUniqueId());

            if (!DefaultConfiguration.rankTimerEnabled.get()) {
                commandSender.sendMessage(Prefix.SYSTEM.def() + "The player §a" + updateP.getName() + "§7 has the rank " + conf.getRank().color() + conf.getRank().displayName() + "§7!");
            } else {
                String timer = String.valueOf(conf.getRankTimer());
                if (timer.equals("-1")) { timer = "infinite"; }
                commandSender.sendMessage(Prefix.SYSTEM.def() + "The player §a" + updateP.getName() + "§7 has the rank " + conf.getRank().color() + conf.getRank().displayName() + "§7 for §a" + JavaTools.convertMinutesToDaysHoursMinutes(Integer.valueOf(timer)) + " §7!");
            }
            return true;
        }

        commandSender.sendMessage(Prefix.SYSTEM.err() + "The Option §c" + option + "§7 was not found!");
        return true;
    }

    public void sendHelp(CommandSender c) {
        c.sendMessage("");
        c.sendMessage(Prefix.SYSTEM.def() + "§a§lHelp:");
        c.sendMessage(Prefix.SYSTEM.def() + "/rank <player> get §8-§7 get a rank of player");
        if (!DefaultConfiguration.rankTimerEnabled.get()) {
            c.sendMessage(Prefix.SYSTEM.def() + "/rank <player> set <rank> §8-§7 set a players rank");
        } else {
            c.sendMessage(Prefix.SYSTEM.def() + "/rank <player> set <rank> <timer> §8-§7 set a players rank");
        }
        c.sendMessage("");
    }
}
