package simpleranks.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simpleranks.utils.Rank;

import java.util.ArrayList;
import java.util.List;

public class BroadcastCommandTabComplete implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> complete = new ArrayList<>();

        if (args.length == 1) {
            Rank.ranks().stream().filter(rank -> rank.displayName().startsWith(args[0])).forEach(rank -> complete.add(rank.displayName()));
        }

        return complete;
    }
}
