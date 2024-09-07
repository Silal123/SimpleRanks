package simpleranks.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simpleranks.Simpleranks;
import simpleranks.utils.Permissions;
import simpleranks.utils.PlayerRank;
import simpleranks.utils.PermissionGroup;
import simpleranks.utils.config.DefaultConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SimpleRanksComandTabComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> complete = new ArrayList<>();

        if (strings.length == 1) {
            if ("info".startsWith(strings[0])) complete.add("info");
            if (commandSender.hasPermission(Permissions.SETUP_RANK_LIST.perm()) || commandSender.hasPermission(Permissions.SETUP_RANK_INFO.perm()) || commandSender.hasPermission(Permissions.SETUP_RANK_DELETE.perm()) ||
            commandSender.hasPermission(Permissions.SETUP_RANK_MODIFY.perm()) || commandSender.hasPermission(Permissions.SETUP_RANK_CREATE.name())) { if ("rank".startsWith(strings[0])) complete.add("rank"); }
            if (commandSender.hasPermission(Permissions.SETUP_GROUP_LIST.perm()) || commandSender.hasPermission(Permissions.SETUP_GROUP_INFO.perm()) || commandSender.hasPermission(Permissions.SETUP_GROUP_DELETE.perm()) ||
                    commandSender.hasPermission(Permissions.SETUP_GROUP_MODIFY.perm()) || commandSender.hasPermission(Permissions.SETUP_GROUP_CREATE.name())) { if ("group".startsWith(strings[0])) complete.add("group"); }
            if (commandSender.hasPermission(Permissions.CONFIG.perm()) && "config".startsWith(strings[0])) complete.add("config");
        }

        if (strings.length == 2 && strings[0].equals("config") && commandSender.hasPermission(Permissions.CONFIG.perm())) {
            if ("defaultRank".startsWith(strings[1])) complete.add("defaultRank");
            if ("chatFormat".startsWith(strings[1])) complete.add("chatFormat");
            if ("chatRank".startsWith(strings[1])) complete.add("chatRank");
            if ("teamSeparator".startsWith(strings[1])) complete.add("teamSeparator");
            if ("rankTimer".startsWith(strings[1])) complete.add("rankTimer");
            if ("teamRank".startsWith(strings[1])) complete.add("teamRank");
            if ("defaultGroup".startsWith(strings[1])) complete.add("defaultGroup");
            if ("teamRankPlayerNameColor".startsWith(strings[1])) complete.add("teamRankPlayerNameColor");
        }

        if (strings.length == 3 && strings[0].equals("config") && (strings[1].equals("chatRank") || strings[1].equals("rankTimer") || strings[1].equals("teamRank"))) {
            if ("true".startsWith(strings[2])) complete.add("true");
            if ("false".startsWith(strings[2])) complete.add("false");
        }

        if (strings.length == 3 && strings[0].equals("config") && strings[1].equals("chatFormat")) {
            complete.add(DefaultConfiguration.chatRankFormat.defaultValue());
        }

        if (strings.length == 3 && strings[0].equals("config") && strings[1].equals("teamRankPlayerNameColor")) {
            complete.addAll(PlayerRank.colors());
        }

        if (strings.length == 3 && strings[0].equals("config") && strings[1].equals("defaultGroup")) {
            for (String name : PermissionGroup.groupNames()) {
                if (!name.startsWith(strings[2])) continue;
                complete.add(name);
            }
        }

        if (strings.length == 3 && strings[0].equals("config") && strings[1].equals("teamSeparator")) {
            complete.add(DefaultConfiguration.teamRankSeparator.defaultValue());
        }

        if (strings.length == 3 && strings[0].equals("config") && strings[1].equals("defaultRank") && commandSender.hasPermission(Permissions.CONFIG.perm())) {
            for (String rankName : PlayerRank.rankNames()) {
                if (!rankName.startsWith(strings[2])) continue;
                complete.add(rankName);
            }
        }

        if (strings.length == 2 && strings[0].equals("rank")) {
            if (commandSender.hasPermission(Permissions.SETUP_RANK_CREATE.perm()) && "create".startsWith(strings[1])) complete.add("create");
            if (commandSender.hasPermission(Permissions.SETUP_RANK_DELETE.perm()) && "delete".startsWith(strings[1])) complete.add("delete");
            if (commandSender.hasPermission(Permissions.SETUP_RANK_INFO.perm()) && "info".startsWith(strings[1])) complete.add("info");
            if (commandSender.hasPermission(Permissions.SETUP_RANK_MODIFY.perm()) && "modify".startsWith(strings[1])) complete.add("modify");
            if (commandSender.hasPermission(Permissions.SETUP_RANK_LIST.perm()) && "list".startsWith(strings[1])) complete.add("list");
        }

        if (strings.length == 2 && strings[0].equals("group")) {
            if (commandSender.hasPermission(Permissions.SETUP_GROUP_CREATE.perm()) && "create".startsWith(strings[1])) complete.add("create");
            if (commandSender.hasPermission(Permissions.SETUP_GROUP_DELETE.perm()) && "delete".startsWith(strings[1])) complete.add("delete");
            if (commandSender.hasPermission(Permissions.SETUP_GROUP_INFO.perm()) && "info".startsWith(strings[1])) complete.add("info");
            if (commandSender.hasPermission(Permissions.SETUP_GROUP_MODIFY.perm()) && "modify".startsWith(strings[1])) complete.add("modify");
            if (commandSender.hasPermission(Permissions.SETUP_GROUP_LIST.perm()) && "list".startsWith(strings[1])) complete.add("list");
        }

        if (strings.length == 3 && strings[1].equals("modify") && commandSender.hasPermission(Permissions.SETUP_RANK_MODIFY.perm())) {
            if (strings[0].equals("group")) {
                for (String name : PermissionGroup.groupNames()) {
                    if (!name.startsWith(strings[2])) continue;
                    complete.add(name);
                }
            }

            if (strings[0].equals("rank")) {
                for (String rankName : PlayerRank.rankNames()) {
                    if (!rankName.startsWith(strings[2])) continue;
                    complete.add(rankName);
                }
            }
        }

        if (strings.length == 4 && strings[1].equals("modify") && commandSender.hasPermission(Permissions.SETUP_RANK_MODIFY.perm())) {
            if (strings[0].equals("group")) {
                if ("addPermission".startsWith(strings[3])) complete.add("addPermission");
                if ("removePermission".startsWith(strings[3])) complete.add("removePermission");
                if ("resetPermissions".startsWith(strings[3])) complete.add("resetPermissions");
                if ("setName".startsWith(strings[3])) complete.add("setName");
            }

            if (strings[0].equals("rank")) {
                if ("setDisplayName".startsWith(strings[3])) complete.add("setDisplayName");
                if ("setColor".startsWith(strings[3])) complete.add("setColor");
                if ("moveUp".startsWith(strings[3])) complete.add("moveUp");
                if ("moveDown".startsWith(strings[3])) complete.add("moveDown");
                if ("setGroup".startsWith(strings[3])) complete.add("setGroup");
            }
        }

        if (strings.length == 5 && strings[0].equals("group") && strings[1].equals("modify") && PermissionGroup.groupNames().contains(strings[2]) && strings[3].equals("addPermission")) {
            PermissionGroup group = PermissionGroup.get(strings[2]);
            for (Permission perm : Simpleranks.instance.getServer().getPluginManager().getPermissions()) {
                if (!perm.getName().startsWith(strings[4])) continue;
                if (group.permissions().contains(perm.getName())) continue;
                complete.add(perm.getName());
            }
        }

        if (strings.length == 5 && strings[0].equals("group") && strings[1].equals("modify") && PermissionGroup.groupNames().contains(strings[2]) && strings[3].equals("removePermission")) {
            for (String perm : PermissionGroup.get(strings[2]).permissions()) {
                if (!perm.startsWith(strings[4])) continue;
                complete.add(perm);
            }
        }

        if (strings.length == 5 && strings[1].equals("modify") && strings[3].equals("setColor") && commandSender.hasPermission(Permissions.SETUP_RANK_MODIFY.perm())) {
            if (strings[0].equals("rank")) {
                complete.addAll(PlayerRank.colors());
            }
        }

        if (strings.length == 5 && strings[1].equals("modify") && strings[3].equals("setGroup") && commandSender.hasPermission(Permissions.SETUP_GROUP_MODIFY.perm())) {
            if (strings[0].equals("rank")) {
                for (String name : PermissionGroup.groupNames()) {
                    if (!name.startsWith(strings[4])) continue;
                    complete.add(name);
                }
            }
        }

        if (strings.length == 3 && strings[1].equals("delete") && commandSender.hasPermission(Permissions.SETUP_RANK_DELETE.perm())) {
            if (strings[0].equals("group")) {
                for (String name : PermissionGroup.groupNames()) {
                    if (!name.startsWith(strings[2])) continue;
                    complete.add(name);
                }
            }

            if (strings[0].equals("rank")) {
                for (String rankName : PlayerRank.rankNames()) {
                    if (!rankName.startsWith(strings[2])) continue;
                    complete.add(rankName);
                }
            }
        }

        if (strings.length == 3 && strings[1].equals("info") && commandSender.hasPermission(Permissions.SETUP_RANK_INFO.perm())) {
            if (strings[0].equals("group")) {
                for (String name : PermissionGroup.groupNames()) {
                    if (!name.startsWith(strings[2])) continue;
                    complete.add(name);
                }
            }

            if (strings[0].equals("rank")) {
                for (String rankName : PlayerRank.rankNames()) {
                    if (!rankName.startsWith(strings[2])) continue;
                    complete.add(rankName);
                }
            }
        }

        if (strings.length == 4 && strings[1].equals("create") && commandSender.hasPermission(Permissions.SETUP_RANK_CREATE.perm())) {
            if (strings[0].equals("rank")) {
                complete.addAll(PlayerRank.colors());
            }

            if (strings[0].equals("group")) {
                complete.add(",");
            }
        }

        if (strings.length == 5 && strings[1].equals("create") && commandSender.hasPermission(Permissions.SETUP_RANK_CREATE.perm())) {
            if (strings[0].equals("rank")) {
                for (String name : PermissionGroup.groupNames()) {
                    if (!name.startsWith(strings[4])) continue;
                    complete.add(name);
                }
            }
        }

        return complete;
    }
}
