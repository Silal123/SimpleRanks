package simpleranks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import simpleranks.Simpleranks;
import simpleranks.system.ScoreboardSystem;
import simpleranks.utils.*;
import simpleranks.utils.config.DefaultConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimpleRanksCommand implements CommandExecutor {

    private boolean hasPermission(CommandSender c) {
        Permissions[] permissionsToCheck = {
                Permissions.SETUP_RANK_INFO, Permissions.SETUP_RANK_DELETE, Permissions.SETUP_RANK_MODIFY,
                Permissions.SETUP_RANK_CREATE, Permissions.SETUP_RANK_LIST,
                Permissions.SETUP_GROUP_INFO, Permissions.SETUP_GROUP_DELETE, Permissions.SETUP_GROUP_MODIFY,
                Permissions.SETUP_GROUP_CREATE, Permissions.SETUP_GROUP_LIST
        };

        boolean hasPermission = false;

        for (Permissions permission : permissionsToCheck) {
            if (c.hasPermission(permission.perm())) {
                hasPermission = true;
                break;
            }
        }

        return hasPermission;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!hasPermission(commandSender)) { sendInfo(commandSender); return true; }
        if (strings.length < 1) { sendHelp(commandSender); return true; }
        String option = strings[0];

        if (option.equals("info")) {
            sendInfo(commandSender);
            return true;
        }

        if (option.equals("config")) {
            if (!commandSender.hasPermission(Permissions.CONFIG.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this command!"); }
            if (strings.length < 3) { sendHelp(commandSender); return true; }
            String config_key = strings[1];
            String config_value = strings[2];

            if (config_key.equals("defaultRank")) {
                if (!PlayerRank.isRankExistent(config_value)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The rank you specified does §cnot exist§7!"); return true; }
                DefaultConfiguration.defaultRank.set(config_value);
                PlayerRank rank = PlayerRank.get(config_value);

                PlayerRank.resortRanks();

                commandSender.sendMessage(Prefix.SYSTEM.def() + "You have §asuccessfully§7 set the rank " + rank.color() + rank.displayName() + " as the default!");
                return true;
            }

            if (config_key.equals("defaultGroup")) {
                if (!PermissionGroup.groupNames().contains(config_value)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The group you specified does §cnot exist§7!"); return true; }
                DefaultConfiguration.defaultRank.set(config_value);

                commandSender.sendMessage(Prefix.SYSTEM.def() + "You have §asuccessfully§7 set the group §a" + config_value + " as the default!");
                return true;
            }

            if (config_key.equals("chatFormat")) {
                StringBuilder chatFormat = new StringBuilder();
                for (int i = 0; i < strings.length; i++) {
                    if (i < 2) continue;
                    chatFormat.append(strings[i]).append(" ");
                }
                DefaultConfiguration.chatRankFormat.set(chatFormat.toString());
                commandSender.sendMessage(Prefix.SYSTEM.def() + "You have §asuccessfully§7 changed the chat format!");
                return true;
            }

            if (config_key.equals("chatRank")) {
                if (!(config_value.equalsIgnoreCase("true") || config_value.equalsIgnoreCase("false"))) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter §atrue§7 or §cfalse§7 as value!"); return true; }
                boolean b = false;
                if (config_value.equalsIgnoreCase("true")) b = true;
                if (config_value.equalsIgnoreCase("false")) b = false;
                DefaultConfiguration.chatRankEnabled.set(b);
                if (b) {
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You have successfully §aactivated§7 the rank in the chat!");
                } else {
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You have successfully §cdeactivated§7 the rank in the chat!");
                }
                return true;
            }

            if (config_key.equals("teamSeparator")) {
                String value_without_colorcodes = config_value;
                for (String c : PlayerRank.colors()) { value_without_colorcodes = value_without_colorcodes.replace("&" + c, ""); }

                if (value_without_colorcodes.length() > 4) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The separator may be a §cmaximum of 4 symbols§7 long!"); return true; }

                config_value = config_value.replace("&", "§");

                DefaultConfiguration.teamRankSeparator.set(config_value);
                commandSender.sendMessage(Prefix.SYSTEM.def() + "The separation is now §c" + config_value + "§7!");
                ScoreboardSystem.reloadAll();
                return true;
            }

            if (config_key.equals("teamRank")) {
                if (!(config_value.equalsIgnoreCase("true") || config_value.equalsIgnoreCase("false"))) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter §atrue§7 or §cfalse§7 as value!"); return true; }
                boolean b = false;
                if (config_value.equalsIgnoreCase("true")) b = true;
                if (config_value.equalsIgnoreCase("false")) b = false;
                DefaultConfiguration.teamRankEnabled.set(b);
                if (b) {
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You have successfully §aactivated§7 the team rank!");
                } else {
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You have successfully §cdeactivated§7 the team rank!");
                }
                ScoreboardSystem.reloadAll();
                return true;
            }

            if (config_key.equals("teamRankPlayerNameColor")) {
                if (!PlayerRank.colors().contains(config_value)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter a §avalid§7 color!"); return true; }
                DefaultConfiguration.teamRankPlayerNameColor.set(config_value);
                commandSender.sendMessage(Prefix.SYSTEM.def() + "You updated the §aPlayer Name Color§7 to §" + config_value + "color§7!");
                ScoreboardSystem.reloadAll();
                return true;
            }

            if (config_key.equals("rankTimer")) {
                if (!(config_value.equalsIgnoreCase("true") || config_value.equalsIgnoreCase("false"))) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter §atrue§7 or §cfalse§7 as value!"); return true; }
                boolean b = false;
                if (config_value.equalsIgnoreCase("true")) b = true;
                if (config_value.equalsIgnoreCase("false")) b = false;
                DefaultConfiguration.rankTimerEnabled.set(b);
                if (b) {
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You have successfully §aactivated§7 the rank timer!");
                } else {
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You have successfully §cdeactivated§7 the rank timer!");
                }
                ScoreboardSystem.reloadAll();
                return true;
            }

            commandSender.sendMessage(Prefix.SYSTEM.err() + "The config value you specified was §cnot found§7!");
            return true;
        }

        if (option.equals("group")) {
            if (strings.length < 2) { sendHelp(commandSender); return true; }
            String option2 = strings[1];

            if (option2.equals("list")) {
                if (!commandSender.hasPermission(Permissions.SETUP_GROUP_LIST.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                commandSender.sendMessage(Prefix.SYSTEM.def() + "List of all permission groups: §a" + PermissionGroup.groupNames() + "§7!");
                return true;
            }

            if (option2.equals("create")) {
                if (!commandSender.hasPermission(Permissions.SETUP_GROUP_CREATE.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                if (PermissionGroup.groups().size() > 100) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The server has reached the §cmaximum§7 number of permission groups!"); }
                if (strings.length < 3) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter a §cname and a color§7! Usage: §a/sr group create <name> <permissions>"); return true; }
                String name = strings[2];

                if (name.length() > 30) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The specified name is too long! Please use a §cmaximum of 30§7 characters!"); return true; }
                if (PermissionGroup.groupNames().contains(name)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "A group with the name §c" + name + "§7 already exists!"); return true; }

                List<String> permissions = new ArrayList<>();
                if (strings.length > 3) {
                    String permissionsString = strings[3];
                    String[] permissionsStringSplit = permissionsString.split(",");
                    for (String t_p : permissionsStringSplit) {
                        Permission perm = Simpleranks.instance.getServer().getPluginManager().getPermission(t_p);
                        if (perm == null) continue;
                        if (!Simpleranks.instance.getServer().getPluginManager().getPermissions().contains(perm)) continue;
                        permissions.add(t_p);
                    }
                }

                PermissionGroup.newGroup(name, permissions);
                commandSender.sendMessage(Prefix.SYSTEM.def() + "You have created the permission group §a" + name + "§7 with the permissions §a" + permissions + "§7!");
                return true;
            }

            if (option2.equals("delete")) {
                if (!commandSender.hasPermission(Permissions.SETUP_GROUP_DELETE.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                if (strings.length < 3) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter the §cname§7 of the rank you would like to delete!"); return true; }
                String name = strings[2];

                if (!PermissionGroup.groupNames().contains(name)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "There is §cno group§7 with the specified name!"); return true; }
                if (DefaultConfiguration.defaultPermissionGroup.get().equals(name)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You cannot §cdelete the default group§7! Change it in the configs!"); return true; }

                PermissionGroup.deleteGroup(PermissionGroup.get(name).id());

                commandSender.sendMessage(Prefix.SYSTEM.def() + "You have successfully deleted the group §c" + name + "§7!");
                return true;
            }

            if (option2.equals("info")) {
                if (!commandSender.hasPermission(Permissions.SETUP_GROUP_INFO.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                if (strings.length < 3) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please §center a group§7 from which you would like to access the information!"); return true; }
                String name = strings[2];
                if (!PermissionGroup.groupNames().contains(name)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "There is §cno group§7 with the specified name!"); return true; }
                PermissionGroup group = PermissionGroup.get(name);

                commandSender.sendMessage("");
                commandSender.sendMessage(Prefix.SYSTEM.def() + "§a§lInformations of the group \"" + group.name() + "\":");
                commandSender.sendMessage(Prefix.SYSTEM.def() + "DisplayName: " + group.name());
                commandSender.sendMessage(Prefix.SYSTEM.def() + "Id: " + group.id());
                commandSender.sendMessage(Prefix.SYSTEM.def() + "Permissions: " + group.permissions());
                commandSender.sendMessage("");
                return true;
            }

            if (option2.equals("modify")) {
                if (!commandSender.hasPermission(Permissions.SETUP_GROUP_MODIFY.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                if (strings.length < 3) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please §center a grouü§7 you would like to modify!"); return true; }
                String name = strings[2];

                if (!PermissionGroup.groupNames().contains(name)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "There is §cno group§7 with the specified name!"); return true; }
                if (strings.length < 4) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please specify an §coption§7 you would like to modify!"); return true; }
                String option3_key = strings[3];

                if (option3_key.equals("resetPermissions")) {
                    PermissionGroup.get(name).setPermissions(new ArrayList<>());
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "The permissions of the group §a" + name + "§7 have been reset!");
                    PermissionsManager.reload();
                    return true;
                }

                if (strings.length < 5) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter a §cvalue§7!"); return true; }
                String option3_value = strings[4];

                if (option3_key.equals("addPermission")) {
                    Permission perm = Simpleranks.instance.getServer().getPluginManager().getPermission(option3_value);
                    if (perm == null) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The specified permission does not §cexist§7!"); return true; }
                    if (!Simpleranks.instance.getServer().getPluginManager().getPermissions().contains(perm)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The specified permission does not §cexist§7!"); return true; }

                    if (PermissionGroup.get(name).permissions().contains(option3_value)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The group already has the §cpermission§7!"); return true; }

                    List<String> perms = PermissionGroup.get(name).permissions();
                    perms.add(option3_value);
                    PermissionGroup.get(name).setPermissions(perms);

                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You added the permission §a" + option3_value + "§7 to the group §a" + name + "§7!");
                    PermissionsManager.reload();
                    return true;
                }

                if (option3_key.equals("removePermission")) {
                    if (!PermissionGroup.get(name).permissions().contains(option3_value)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The group §a" + name + "§7 doesn`t have the Permission!"); return true; }

                    List<String> perms = PermissionGroup.get(name).permissions();
                    perms.remove(option3_value);
                    PermissionGroup.get(name).setPermissions(perms);

                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You removed the permission §a" + option3_value + "§7 from the group §a" + name + "§7!");
                    PermissionsManager.reload();
                    return true;
                }

                if (option3_key.equals("setName")) {
                    if (option3_value.length() > 30) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The specified name is too long! Please use a §cmaximum of 30§7 characters!"); return true; }
                    if (PermissionGroup.groupNames().contains(option3_value)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "A group with the name §c" + option3_value + "§7 already exists!"); return true; }
                    try {
                        PermissionGroup.get(name).setName(option3_value);
                        if (DefaultConfiguration.defaultPermissionGroup.get().equals(name)) DefaultConfiguration.defaultPermissionGroup.set(option3_value);
                    } catch (Exception e) { e.printStackTrace(); }
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You changed the name of the group §c" + name + "§7 to §a" + option3_value + "§7!");
                    return true;
                }

                return true;
            }
        }

        if (option.equals("rank")) {
            if (strings.length < 2) { sendHelp(commandSender); return true; }
            String option2 = strings[1];

            if (option2.equals("list")) {
                if (!commandSender.hasPermission(Permissions.SETUP_RANK_LIST.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                List<PlayerRank> ranks = new ArrayList<>(PlayerRank.ranks());
                ranks.sort(Comparator.comparing(PlayerRank::position));

                commandSender.sendMessage("");
                commandSender.sendMessage(Prefix.SYSTEM.def() + "§a§lAll Ränge:§r");
                for (PlayerRank rank : ranks) {
                    commandSender.sendMessage(Prefix.SYSTEM.def() + rank.position() + " - " + rank.color() + rank.displayName() + "§7");
                }
                commandSender.sendMessage("");
                return true;
            }

            if (option2.equals("create")) {
                if (!commandSender.hasPermission(Permissions.SETUP_RANK_CREATE.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                if (PlayerRank.ranks().size() > 50) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The server has reached the §cmaximum§7 number of ranks!"); return true; }
                if (strings.length < 4) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter a §cname and a color§7! Usage: §a/sr rank create <displayName> <color>"); return true; }
                String dpName = strings[2];
                String color = strings[3];

                if (dpName.length() > 30) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The specified name is too long! Please use a §cmaximum of 30§7 characters!"); return true; }
                if (color.length() > 1) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The color must be a §csingle letter§7!"); return true; }
                if (!"4c6e2ab319d5f780".contains(color)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The specified color does §cnot exist§7!"); return true; }
                if (PlayerRank.isRankExistent(dpName)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "A rank with the name §c" + dpName + "§7 already exists!"); return true; }

                PlayerRank rank = PlayerRank.newRank(dpName, color);
                if (strings.length > 4) {
                    if (!PermissionGroup.groupNames().contains(strings[4])) { commandSender.sendMessage(Prefix.SYSTEM.err() + "There is §cno group§7 with the specified name!"); return true; }
                    rank.setGroup(PermissionGroup.get(strings[4]));
                }
                commandSender.sendMessage(Prefix.SYSTEM.def() + "You have created the rank §" + color + dpName + "§7!");
                return true;
            }

            if (option2.equals("delete")) {
                if (!commandSender.hasPermission(Permissions.SETUP_RANK_DELETE.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                if (strings.length < 3) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter the §cname§7 of the rank you would like to delete!"); return true; }
                String dpName = strings[2];

                if (!PlayerRank.isRankExistent(dpName)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "There is §cno rank§7 with the specified name!"); return true; }
                if (DefaultConfiguration.defaultRank.get().equals(dpName)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You cannot §cdelete the default rank§7! Change it in the configs!"); return true; }

                PlayerRank.deleteRank(PlayerRank.get(dpName).id());
                PlayerRank.resortRanks();
                ScoreboardSystem.reloadAll();

                commandSender.sendMessage(Prefix.SYSTEM.def() + "You have successfully deleted the rank §c" + dpName + "§7!");
                return true;
            }

            if (option2.equals("modify")) {
                if (!commandSender.hasPermission(Permissions.SETUP_RANK_MODIFY.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                if (strings.length < 3) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please §center a rank§7 you would like to modify!"); return true; }
                String rankName = strings[2];

                if (!PlayerRank.isRankExistent(rankName)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "There is §cno rank§7 with the specified name!"); return true; }
                if (strings.length < 4) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please specify an §coption§7 you would like to modify!"); return true; }
                String option3_key = strings[3];

                if (option3_key.equals("moveDown")) {
                    PlayerRank rank = PlayerRank.get(rankName);
                    if (rank.position() > PlayerRank.ranks().size() - 2) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The rank is already at the §cbottom§7!"); return true; }

                    PlayerRank upRank = PlayerRank.get(rank.position() + 1);
                    if (upRank != null) upRank.setPosition(upRank.position() -1);

                    rank.setPosition(rank.position() +1);

                    PlayerRank downRank = PlayerRank.get(rank.position() +1);

                    String ranks = "§8...§7, ";
                    if (upRank != null) { ranks += upRank.color() + upRank.displayName() + "§7, "; } else { ranks += "§0none§7, "; }
                    ranks += rank.color() + rank.displayName() + "§7, ";
                    if (downRank != null) { ranks += downRank.color() + downRank.displayName() + "§7, "; } else { ranks += "§0none§7, "; }
                    ranks += "§8...§7";

                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You moved the rank " + rank.color() + rank.displayName() + "§7 down to the position §a" + rank.position() + "§7! " + ranks);
                    ScoreboardSystem.reloadAll();
                    return true;
                }

                if (option3_key.equals("moveUp")) {
                    PlayerRank rank = PlayerRank.get(rankName);
                    if (rank.position() < 1) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The rank is already at the §ctop§7!"); return true; }

                    PlayerRank downRank = PlayerRank.get(rank.position() - 1);
                    if (downRank != null) downRank.setPosition(downRank.position() +1);

                    rank.setPosition(rank.position() -1);

                    PlayerRank upRank = PlayerRank.get(rank.position() - 1);

                    String ranks = "§8...§7, ";
                    if (upRank != null) { ranks += upRank.color() + upRank.displayName() + "§7, "; } else { ranks += "§0none§7, "; }
                    ranks += rank.color() + rank.displayName() + "§7, ";
                    if (downRank != null) { ranks += downRank.color() + downRank.displayName() + "§7, "; } else { ranks += "§0none§7, "; }
                    ranks += "§8...§7";

                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You moved the rank " + rank.color() + rank.displayName() + "§7 up to the position §a" + rank.position() + "§7! " + ranks);
                    ScoreboardSystem.reloadAll();
                    return true;
                }

                if (strings.length < 5) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please enter a §cvalue§7!"); return true; }
                String option3_value = strings[4];

                if (option3_key.equals("setDisplayName")) {
                    if (option3_value.length() > 30) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The specified name is too long! Please use a §cmaximum of 30§7 characters!"); return true; }
                    if (PlayerRank.isRankExistent(option3_value)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "A rank with the name §c" + option3_value + "§7 already exists!"); return true; }
                    try {
                        PlayerRank.get(rankName).setDisplayName(option3_value);
                        if (DefaultConfiguration.defaultRank.get().equals(rankName)) DefaultConfiguration.defaultRank.set(option3_value);
                    } catch (Exception e) { e.printStackTrace(); }
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You changed the name of the rank §c" + rankName + "§7 to §a" + option3_value + "§7!");
                    ScoreboardSystem.reloadAll();
                    return true;
                }

                if (option3_key.equals("setColor")) {
                    if (option3_value.length() > 1) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The color must be a §csingle letter§7!"); return true; }
                    if (!"4c6e2ab319d5f780".contains(option3_value)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "The specified color does §cnot exist§7!"); return true; }
                    String oldC = PlayerRank.get(rankName).color();
                    PlayerRank.get(rankName).setColor(option3_value);
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You changed the rank color from " + oldC + "color§7 to §" + option3_value + "color§7!");
                    ScoreboardSystem.reloadAll();
                    return true;
                }

                if (option3_key.equals("setGroup")) {
                    if (!PermissionGroup.groupNames().contains(option3_value)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "A rank with the name §c" + option3_value + "§7 does not exists!"); return true; }
                    PlayerRank.get(rankName).setGroup(PermissionGroup.get(option3_value));
                    commandSender.sendMessage(Prefix.SYSTEM.def() + "You changed the group of the rank to §a" + option3_value + "§7!");
                    PermissionsManager.reload();
                    return true;
                }

                commandSender.sendMessage(Prefix.SYSTEM.err() + "The modify option §c" + option3_key + "§7 was not found!");
                return true;
            }

            if (option2.equals("info")) {
                if (!commandSender.hasPermission(Permissions.SETUP_RANK_INFO.perm())) { commandSender.sendMessage(Prefix.SYSTEM.err() + "You are §cnot allowed§7 to execute this subcommand!"); return true; }
                if (strings.length < 3) { commandSender.sendMessage(Prefix.SYSTEM.err() + "Please §center a rank§7 from which you would like to access the information!"); return true; }
                String rankName = strings[2];
                if (!PlayerRank.isRankExistent(rankName)) { commandSender.sendMessage(Prefix.SYSTEM.err() + "There is §cno rank§7 with the specified name!"); return true; }
                PlayerRank rank = PlayerRank.get(rankName);

                commandSender.sendMessage("");
                commandSender.sendMessage(Prefix.SYSTEM.def() + "§a§lInformations of the rank \"" + rank.displayName() + "\":");
                commandSender.sendMessage(Prefix.SYSTEM.def() + "DisplayName: " + rank.displayName());
                commandSender.sendMessage(Prefix.SYSTEM.def() + "Id: " + rank.id());
                commandSender.sendMessage(Prefix.SYSTEM.def() + "Color: " + rank.color().replace("§", ""));
                commandSender.sendMessage(Prefix.SYSTEM.def() + "Position: " + rank.position());
                commandSender.sendMessage(Prefix.SYSTEM.def() + "Group: " + rank.group().name());
                commandSender.sendMessage("");
                return true;
            }

            commandSender.sendMessage(Prefix.SYSTEM.err() + "The rank option you specified does §xnot exist§7!");
            return true;
        }

        commandSender.sendMessage(Prefix.SYSTEM.err() + "The Option §c" + option + "§7 was not found!");
        return true;
    }

    public void sendHelp(CommandSender s) {
        s.sendMessage("");
        s.sendMessage(Prefix.SYSTEM.def() + "§a§lHelp:");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr rank create <name> <color> §8-§7 create a new rank");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr rank create <name> <color> <group> §8-§7 create a new rank with permission group");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr rank delete <name> §8-§7 delete a rank");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr rank info <name> §8-§7 get the info of Rank");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr rank modify <name> <moveUp/moveDown> §8-§7 move a rank position up and down");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr rank modify <name> <key> <value> §8-§7 modify a ranks data");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr rank list §8-§7 get a list of all ranks");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr group create <name> <permissions> §8-§7 create an permission group");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr group modify <name> <option> somtimes:<value> §8-§7 edit properties");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr group info <name> §8-§7 shows the Group Info");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr group list §8-§7 list of all groups");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr group delete <name> §8-§7 delete a group");
        s.sendMessage(Prefix.SYSTEM.def() + "/sr config <key> <value> §8-§7 edit config");
        s.sendMessage("");
    }

    public void sendInfo(CommandSender s) {
        PluginDescriptionFile meta = Simpleranks.instance.getDescription();
        s.sendMessage("");
        s.sendMessage(Prefix.SYSTEM.def() +"§a§lPlugin Information:");
        s.sendMessage(Prefix.SYSTEM.def() + "Version: " + meta.getVersion());
        s.sendMessage(Prefix.SYSTEM.def() + "Authors: " + meta.getAuthors());
        s.sendMessage(Prefix.SYSTEM.def() + "Name: " + meta.getName());
        s.sendMessage(Prefix.SYSTEM.def() + "Website: " + meta.getWebsite());
        s.sendMessage("");
    }
}
