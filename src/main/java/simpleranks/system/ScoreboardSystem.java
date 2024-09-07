package simpleranks.system;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import simpleranks.utils.PlayerRank;
import simpleranks.utils.config.DefaultConfiguration;
import simpleranks.utils.config.PlayerConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardSystem {

    public static Map<UUID, ScoreboardSystem> loggedScoreboards = new HashMap<>();

    private final UUID uuid;
    public ScoreboardSystem(Player owner) {
        this.uuid = owner.getUniqueId();
        if (loggedScoreboards.containsKey(owner.getUniqueId())) { loggedScoreboards.remove(owner.getUniqueId()); }
        loggedScoreboards.put(owner.getUniqueId(), this);
        this.reload();
        reloadAll();
    }

    public void loadMain() {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;

        Scoreboard scoreboard = p.getScoreboard();

        scoreboard.getTeams().forEach(team -> { team.getPlayers().forEach(team::removePlayer); });
        scoreboard.getTeams().forEach(Team::unregister);

        if (!DefaultConfiguration.teamRankEnabled.get()) return;

        for (PlayerRank this_rank : PlayerRank.ranks()) {
            Team t = scoreboard.registerNewTeam(this_rank.teamName());
            t.setPrefix(this_rank.color() + this_rank.displayName() + "§8 " + DefaultConfiguration.teamRankSeparator.get() + " §7");

            if (!PlayerRank.colors().contains(DefaultConfiguration.teamRankPlayerNameColor.get())) t.setColor(ChatColor.getByChar('7'));
            else t.setColor(ChatColor.getByChar(DefaultConfiguration.teamRankPlayerNameColor.get()));
        }

        for (Player this_player : Bukkit.getOnlinePlayers()) {
            PlayerRank this_rank = PlayerConfiguration.getFor(this_player).getRank();
            scoreboard.getTeam(this_rank.teamName()).addPlayer(this_player);
        }

        p.setScoreboard(scoreboard);
    }

    public void reload() {
        this.loadMain();
    }

    public static void reloadAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ScoreboardSystem s = getScoreboardSystemForPlayer(p);
            s.reload();
        }
    }

    public static ScoreboardSystem getScoreboardSystemForPlayer(Player player) {
        if (player == null) return null;
        if (!loggedScoreboards.containsKey(player.getUniqueId())) return new ScoreboardSystem(player);
        return loggedScoreboards.get(player.getUniqueId());
    }

    public static void playerChatEvent(AsyncPlayerChatEvent e) {
        if (!DefaultConfiguration.chatRankEnabled.get()) return;

        PlayerConfiguration conf = PlayerConfiguration.getFor(e.getPlayer());
        PlayerRank rank = conf.getRank();

        String format = DefaultConfiguration.chatRankFormat.get().replace("&", "§");
        String message = format.replace("{rank_color}", rank.color()).replace("{rank_dpname}", rank.displayName()).replace("{player_name}", e.getPlayer().getName()).replace("{message}", e.getMessage());
        e.setFormat(message);
    }

    public static void playerJoinEvent(PlayerJoinEvent e) {
        reloadAll();
    }

    public static void playerLeaveEvent(PlayerQuitEvent e) {
        if (loggedScoreboards.containsKey(e.getPlayer().getUniqueId())) { loggedScoreboards.remove(e.getPlayer().getUniqueId()); }
        reloadAll();
    }

}
