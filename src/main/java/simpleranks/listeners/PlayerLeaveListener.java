package simpleranks.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import simpleranks.system.ScoreboardSystem;
import simpleranks.utils.PermissionsManager;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        ScoreboardSystem.playerLeaveEvent(e);
        PermissionsManager.removePlayerAttachment(e.getPlayer());
    }

}
