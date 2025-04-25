package simpleranks.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import simpleranks.SimpleRanks;
import simpleranks.system.ScoreboardSystem;
import simpleranks.utils.PermissionsManager;
import simpleranks.utils.config.DefaultConfiguration;
import simpleranks.utils.config.PlayerConfiguration;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (DefaultConfiguration.joinMessageEnabled.get()) {
            PlayerConfiguration config = PlayerConfiguration.getFor(e.getPlayer());
            String format = DefaultConfiguration.joinMessageFormat.get();
            format = format.replace("&", "§")
                    .replace("%rank_color%", config.getRank().color())
                    .replace("%rank_dpname%", config.getRank().displayName())
                    .replace("%player_name%", e.getPlayer().getName());

            if (SimpleRanks.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = PlaceholderAPI.setPlaceholders(e.getPlayer(), format);
            }

            e.setJoinMessage(format);
        }
        ScoreboardSystem.playerJoinEvent(e);
        PermissionsManager.reload();
    }

}
