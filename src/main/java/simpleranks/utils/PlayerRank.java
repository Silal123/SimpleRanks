package simpleranks.utils;

import simpleranks.utils.config.DefaultConfiguration;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PlayerRank extends Database {

    private long id;

    public PlayerRank(long id) {
        this.id = id;
    }

    public static PlayerRank newRank(String dpn, String color) {
        long id = new Random().nextLong(); id = Math.abs(id) % 10000000000000000L;
        List<Long> ranks = PlayerRank.rankIds();
        while (ranks.contains(id)) { id = new Random().nextLong(); id = Math.abs(id) % 10000000000000000L; }

        int position = - 1;
        if (dpn.equals(DefaultConfiguration.defaultRank.get())) {
            position = rankNames().size() + 2;
        } else {
            if (!PlayerRank.rankNames().isEmpty()) {
                if (PlayerRank.rankNames().contains(getDefaultRank().displayName())) { position = position-1; }
            }
        }

        try {
            database.executeUpdate("INSERT INTO " + ranksDataTable + " (`id`, `displayName`, `color`, `position`) VALUES ('" + id + "', '" + dpn + "', '" + color + "', '" + position + "')");
        } catch (Exception e) { e.printStackTrace(); }
        resortRanks();
        return PlayerRank.get(id);
    }

    public static void deleteRank(long id) {
        if (!isRankExistent(id)) return;
        try {
            database.executeUpdate("DELETE FROM " + ranksDataTable + " WHERE id = '" + id + "';");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static PlayerRank get(long id) {
        return new PlayerRank(id);
    }
    public static PlayerRank get(String name) {
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + " WHERE displayName = '" + name + "';");
            String temp_id = null; if (rs.next()) { temp_id = rs.getString("id"); }
            rs.close();
            if (temp_id == null) return PlayerRank.getDefaultRank();
            if (!JavaTools.isLong(temp_id)) return PlayerRank.getDefaultRank();
            return PlayerRank.get(Long.valueOf(temp_id));
        } catch (Exception e) { e.printStackTrace(); }
        return PlayerRank.getDefaultRank();
    }

    public static PlayerRank get(int position) {
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + " WHERE position = '" + position + "';");
            String temp_id = null; if (rs.next()) { temp_id = rs.getString("id"); }
            rs.close();
            if (temp_id == null) return null;
            if (!JavaTools.isLong(temp_id)) return null;
            return PlayerRank.get(Long.valueOf(temp_id));
        } catch (Exception e) { e.printStackTrace(); }
        return PlayerRank.getDefaultRank();
    }


    public long id() { return id; }
    public String displayName() {
        if (!isRankExistent(id)) return null;
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + " WHERE id = '" + id + "';");
            String s = null; if (rs.next()) { s = rs.getString("displayName"); }
            rs.close();
            return s;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public PlayerRank setDisplayName(String name) {
        try {
            database.executeUpdate("UPDATE " + ranksDataTable + " SET displayName = '" + name + "' WHERE id = '" + id + "';");
        } catch (Exception e) { e.printStackTrace(); }
        return this;
    }
    public String teamName() {
        int pos = position();
        String ps = "";
        while (pos > 9) {
            pos = pos-9;
            ps += "9";
        }
        ps += pos;
        return ps + "-" + displayName().toLowerCase();
    }
    public int position() {
        if (!isRankExistent(id)) return 99999;
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + " WHERE id = '" + id + "';");
            int i = 99999; if (rs.next()) { i = rs.getInt("position"); }
            rs.close();
            return i;
        } catch (Exception e) { e.printStackTrace(); }
        return 99999;
    }
    public PlayerRank setPosition(int pos) {
        try {
            database.executeUpdate("UPDATE " + ranksDataTable + " SET position = '" + pos + "' WHERE id = '" + id + "';");
        } catch (Exception e) { e.printStackTrace(); }
        return this;
    }

    public String color() {
        if (!isRankExistent(id)) return null;
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + " WHERE id = '" + id + "';");
            String s = null; if (rs.next()) { s = rs.getString("color"); }
            rs.close();
            return "§" + s;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    public PlayerRank setColor(String color) {
        try {
            database.executeUpdate("UPDATE " + ranksDataTable + " SET color = '" + color + "' WHERE id = '" + id + "';");
        } catch (Exception e) { e.printStackTrace(); }
        return this;
    }

    public PlayerRank setGroup(PermissionGroup group) {
        try {
            database.executeUpdate("UPDATE " + ranksDataTable + " SET `group` = '" + group.id() + "' WHERE id = '" + id + "';");
        } catch (Exception e) { e.printStackTrace(); }
        return this;
    }
    public PermissionGroup group() {
        if (!isRankExistent(id)) return null;
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + " WHERE id = '" + id + "';");
            String s = null; if (rs.next()) { s = rs.getString("group"); }
            rs.close();
            if (s == null) { return PermissionGroup.getDefaultGroup(); }
            if (!PermissionGroup.groupIds().contains(Long.valueOf(s))) { setGroup(PermissionGroup.getDefaultGroup()); return PermissionGroup.getDefaultGroup(); }
            return PermissionGroup.get(Long.valueOf(s));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }


    public static List<PlayerRank> ranks() {
        List<PlayerRank> re = new ArrayList<>();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + ";");
            while (rs.next()) {
                String tmp_id = rs.getString("id");
                if (tmp_id == null) continue;
                if (!JavaTools.isLong(tmp_id)) continue;
                re.add(PlayerRank.get(Long.valueOf(tmp_id)));
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
        return re;
    }
    public static List<Long> rankIds() {
        List<Long> re = new ArrayList<>();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + ";");
            while (rs.next()) {
                String tmp_id = rs.getString("id");
                if (tmp_id == null) continue;
                if (!JavaTools.isLong(tmp_id)) continue;
                re.add(Long.valueOf(tmp_id));
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
        return re;
    }
    public static List<String> rankNames() {
        List<String> re = new ArrayList<>();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + ";");
            while (rs.next()) {
                String dpN = rs.getString("displayName");
                if (dpN == null) continue;
                re.add(dpN);
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
        return re;
    }

    public static List<String> colors() {
        return List.of("4", "c", "6", "e", "2", "a", "b", "3", "1", "9", "d", "5", "f", "7", "8", "9");
    }

    public static PlayerRank getDefaultRank() {
        if (!isRankExistent(DefaultConfiguration.defaultRank.get())) {
            PlayerRank rank = newRank(DefaultConfiguration.defaultRank.get(), "f").setPosition(rankNames().size() + 2);
            resortRanks();
            return rank;
        } else {
            if (PlayerRank.get(DefaultConfiguration.defaultRank.get()).position() == 10000) { PlayerRank.get(DefaultConfiguration.defaultRank.get()).setPosition(rankNames().size() + 2); resortRanks(); }
            return PlayerRank.get(DefaultConfiguration.defaultRank.get());
        }
    }



    public static boolean isRankExistent(long id) {
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + " WHERE id = '" + id + "';");
            boolean b = rs.next();
            rs.close();
            return b;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean isRankExistent(String dpN) {
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksDataTable + " WHERE displayName = '" + dpN + "';");
            boolean b = rs.next();
            rs.close();
            return b;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }


    public static void resortRanks() {
        List<PlayerRank> ranks = new ArrayList<>(PlayerRank.ranks());
        ranks.sort(Comparator.comparing(PlayerRank::position));
        for (int i = 0; i < ranks.size(); i++) {
            PlayerRank r = ranks.get(i);
            r.setPosition(i);
        }
    }

    public static void init() {
        try {
            getDefaultRank();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
