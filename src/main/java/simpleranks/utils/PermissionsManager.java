package simpleranks.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import simpleranks.Simpleranks;
import simpleranks.utils.config.PlayerConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PermissionsManager {

    public static Map<UUID, PermissionAttachment> permissionAttachments = new HashMap<>();

    public static void addPermissionToPlayer(Player p, String permission) {
        if (!permissionAttachments.containsKey(p.getUniqueId())) permissionAttachments.put(p.getUniqueId(), p.addAttachment(Simpleranks.instance));
        PermissionAttachment attachment = permissionAttachments.get(p.getUniqueId());
        attachment.setPermission(permission, true);
    }

    public static void removePermissionFromPlayer(Player p, String permission) {
        if (!permissionAttachments.containsKey(p.getUniqueId())) return;
        PermissionAttachment attachment = permissionAttachments.get(p.getUniqueId());
        attachment.unsetPermission(permission);
    }


    public static void removePlayerAttachment(Player p) {
        if (!permissionAttachments.containsKey(p.getUniqueId())) return;
        p.removeAttachment(permissionAttachments.get(p.getUniqueId()));
        permissionAttachments.remove(p.getUniqueId());
    }

    public static void removeAllPermissionsFromPlayer(Player p) {
        if (!permissionAttachments.containsKey(p.getUniqueId())) return;
        for (Map.Entry<String, Boolean> entry : permissionAttachments.get(p.getUniqueId()).getPermissions().entrySet()) {
            permissionAttachments.get(p.getUniqueId()).unsetPermission(entry.getKey());
        }
    }

    public static void removeAllPlayerAttachments() {
        Map<UUID, PermissionAttachment> save = new HashMap<>(permissionAttachments);
        for (Map.Entry<UUID, PermissionAttachment> entry : save.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p == null) {
                permissionAttachments.remove(entry.getKey());
                continue;
            }

            p.removeAttachment(entry.getValue());
            permissionAttachments.remove(p.getUniqueId());
        }
    }

    public static void addPermissionGroupPermissionsToPlayer(Player p) {
        PermissionGroup group = PlayerConfiguration.getFor(p).getRank().group();
        removeAllPermissionsFromPlayer(p);
        for (String perm : group.permissions()) {
            addPermissionToPlayer(p, perm);
        }
    }

    public static void reload() {
        removeAllPlayerAttachments();
        for (Player p : Bukkit.getOnlinePlayers()) {
            addPermissionGroupPermissionsToPlayer(p);
        }
    }
}
