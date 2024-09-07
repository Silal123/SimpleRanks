package simpleranks.utils.config;

import org.bukkit.entity.Player;
import simpleranks.utils.Database;
import simpleranks.utils.JavaTools;
import simpleranks.utils.PlayerRank;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerConfiguration extends Database {

    private UUID playerUUID;
    public PlayerConfiguration(UUID uuid) {
        this.playerUUID = uuid;
    }


    public static PlayerConfiguration getFor(Player p) {
        return new PlayerConfiguration(p.getUniqueId());
    }

    public static PlayerConfiguration getFor(UUID uuid) {
        return new PlayerConfiguration(uuid);
    }


    public boolean hasPlayer() {
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + playerDataTable + " WHERE uuid = '" + playerUUID + "';");
            boolean b = rs.next();
            rs.close();
            return b;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public void setRank(PlayerRank rank) {
        try {
            if (hasPlayer()) {
                database.executeUpdate("UPDATE " + playerDataTable + " SET rank = '" + rank.id() + "' WHERE uuid = '" + playerUUID + "';");
            } else {
                database.executeUpdate("INSERT INTO " + playerDataTable + " (`uuid`, `rank`) VALUES ('" + playerUUID + "', '" + rank.id() + "')");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public PlayerRank getRank() {
        if (!hasPlayer()) return PlayerRank.getDefaultRank();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + playerDataTable + " WHERE uuid = '" + playerUUID + "';");
            String temp_id = null; if (rs.next()) { temp_id = rs.getString("rank"); }
            rs.close();
            if (temp_id == null) return PlayerRank.getDefaultRank();
            if (!JavaTools.isLong(temp_id)) return PlayerRank.getDefaultRank();
            long id = Long.valueOf(temp_id);
            if (!PlayerRank.isRankExistent(id)) return PlayerRank.getDefaultRank();
            return PlayerRank.get(id);
        } catch (Exception e) { e.printStackTrace(); }
        return PlayerRank.getDefaultRank();
    }

    public void setRankTimer(int timer) {
        try {
            if (hasPlayer()) {
                database.executeUpdate("UPDATE " + playerDataTable + " SET timer = '" + timer + "' WHERE uuid = '" + playerUUID + "';");
            } else {
                database.executeUpdate("INSERT INTO " + playerDataTable + " (`uuid`, `rank`, `timer`) VALUES ('" + playerUUID + "', '" + PlayerRank.getDefaultRank().id() + "', '" + timer + "')");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public int getRankTimer() {
        if (!hasPlayer()) return -1;
        if (!DefaultConfiguration.rankTimerEnabled.get()) return -1;
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + playerDataTable + " WHERE uuid = '" + playerUUID + "';");
            int timer = -1; if (rs.next()) { timer = rs.getInt("timer"); }
            rs.close();
            return timer;
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }


    public static List<PlayerConfiguration> playersInDatabase() {
        List<PlayerConfiguration> re = new ArrayList<>();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + playerDataTable + ";");
            while (rs.next()) {
                re.add(PlayerConfiguration.getFor(UUID.fromString(rs.getString("uuid"))));
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
        return re;
    }
}
