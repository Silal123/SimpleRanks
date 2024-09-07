package simpleranks.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import simpleranks.system.ScoreboardSystem;
import simpleranks.utils.PermissionsManager;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ScoreboardSystem.playerJoinEvent(e);
        PermissionsManager.reload();
    }

}
