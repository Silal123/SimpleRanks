package simpleranks.commands.tabcomplete;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simpleranks.utils.Permissions;
import simpleranks.utils.PlayerRank;
import simpleranks.utils.config.DefaultConfiguration;

import java.util.ArrayList;
import java.util.List;

public class RankCommandTabComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> complete = new ArrayList<>();

        if (strings.length == 1) {
            if (commandSender.hasPermission(Permissions.GET_RANK.perm()) || commandSender.hasPermission(Permissions.SET_RANK.perm())) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getName().startsWith(strings[0])) continue;
                    complete.add(player.getName());
                }
            }
        }

        if (strings.length == 2) {
            if (commandSender.hasPermission(Permissions.GET_RANK.perm())) {
                if ("get".startsWith(strings[1])) complete.add("get");
            }

            if (commandSender.hasPermission(Permissions.SET_RANK.perm())) {
                if ("set".startsWith(strings[1])) complete.add("set");
            }
        }

        if (strings.length == 3 && strings[1].equals("set")) {
            if (commandSender.hasPermission(Permissions.SET_RANK.perm())) {
                for (String rankName : PlayerRank.rankNames()) {
                    if (!rankName.startsWith(strings[2])) continue;
                    complete.add(rankName);
                }
            }
        }

        if (strings.length == 4 && strings[1].equals("set")) {
            if (DefaultConfiguration.rankTimerEnabled.get()) {
                if ("infinite".startsWith(strings[3])) complete.add("infinite");
                if ("1".startsWith(strings[3])) complete.add("1");
                if ("2".startsWith(strings[3])) complete.add("2");
                if ("3".startsWith(strings[3])) complete.add("3");
                if ("4".startsWith(strings[3])) complete.add("4");
                if ("5".startsWith(strings[3])) complete.add("5");
                if ("6".startsWith(strings[3])) complete.add("6");
                if ("7".startsWith(strings[3])) complete.add("7");
                if ("8".startsWith(strings[3])) complete.add("8");
            }
        }

        return complete;
    }
}
