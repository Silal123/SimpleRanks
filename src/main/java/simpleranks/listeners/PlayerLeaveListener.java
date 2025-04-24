package simpleranks.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import simpleranks.Simpleranks;
import simpleranks.system.ScoreboardSystem;
import simpleranks.utils.PermissionsManager;
import simpleranks.utils.config.DefaultConfiguration;
import simpleranks.utils.config.PlayerConfiguration;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (DefaultConfiguration.quitMessageEnabled.get()) {
            PlayerConfiguration config = PlayerConfiguration.getFor(e.getPlayer());
            String format = DefaultConfiguration.quitMessageFormat.get();
            format = format.replace("&", "§")
                    .replace("%rank_color%", config.getRank().color())
                    .replace("%rank_dpname%", config.getRank().displayName())
                    .replace("%player_name%", e.getPlayer().getName());

            if (Simpleranks.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = PlaceholderAPI.setPlaceholders(e.getPlayer(), format);
            }

            e.setQuitMessage(format);
        }
        ScoreboardSystem.playerLeaveEvent(e);
        PermissionsManager.removePlayerAttachment(e.getPlayer());
    }

}
