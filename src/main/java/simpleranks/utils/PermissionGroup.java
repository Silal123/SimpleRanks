package simpleranks.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import simpleranks.utils.config.DefaultConfiguration;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PermissionGroup extends Database {

    private long id;

    public PermissionGroup(long id) {
        this.id = id;
    }

    public static String convertPermissionListToJson(List<String> list) {
        return new Gson().toJson(list);
    }

    public static List<String> convertJsonToPermissionList(String json) {
        return new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
    }

    public static PermissionGroup newGroup(String name, List<String> permissions) {
        long id = new Random().nextLong(); id = Math.abs(id) % 10000000000000000L;
        while (isGroupExistent(id)) { id = new Random().nextLong(); id = Math.abs(id) % 10000000000000000L; }

        if (groupNames().contains(name)) return null;

        try {
            database.executeUpdate("INSERT INTO " + ranksPermissionGroupTable + " (`id`, `name`, `permissions`) VALUES ('" + id + "', '" + name + "', '" + convertPermissionListToJson(permissions) + "')");
        } catch (Exception e) { e.printStackTrace(); }
        return PermissionGroup.get(id);
    }

    public static void deleteGroup(long id) {
        if (!isGroupExistent(id)) return;
        try {
            database.executeUpdate("DELETE FROM " + ranksPermissionGroupTable + " WHERE id = '" + id + "';");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static PermissionGroup get(long id) {
        return new PermissionGroup(id);
    }
    public static PermissionGroup get(String name) {
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksPermissionGroupTable + " WHERE name = '" + name + "';");
            String temp_id = null; if (rs.next()) { temp_id = rs.getString("id"); }
            rs.close();
            if (temp_id == null) return null;
            if (!JavaTools.isLong(temp_id)) return null; //TODO Fix nulls
            return PermissionGroup.get(Long.valueOf(temp_id));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public long id() { return id; }
    public String name() {
        if (!isGroupExistent(id)) return null;
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksPermissionGroupTable + " WHERE id = '" + id + "';");
            String s = null; if (rs.next()) { s = rs.getString("name"); }
            rs.close();
            return s;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public void setName(String name) {
        if (!isGroupExistent(id)) return;
        try {
            database.executeUpdate("UPDATE " + ranksPermissionGroupTable + " SET name = '" + name + "' WHERE id = '" + id + "';");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<String> permissions() {
        if (!isGroupExistent(id)) return new ArrayList<>();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksPermissionGroupTable + " WHERE id = '" + id + "';");
            String s = null; if (rs.next()) { s = rs.getString("permissions"); }
            rs.close();
            if (!JsonManager.isValidJson(s)) return new ArrayList<>();
            return convertJsonToPermissionList(s);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    public void setPermissions(List<String> permissions) {
        if (!isGroupExistent(id)) return;
        try {
            database.executeUpdate("UPDATE " + ranksPermissionGroupTable + " SET permissions = '" + convertPermissionListToJson(permissions) + "' WHERE id = '" + id + "';");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static boolean isGroupExistent(long id) {
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksPermissionGroupTable + " WHERE id = '" + id + "';");
            boolean b = rs.next();
            rs.close();
            return b;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }


    public static List<PermissionGroup> groups() {
        List<PermissionGroup> re = new ArrayList<>();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksPermissionGroupTable + ";");
            while (rs.next()) {
                String tmp_id = rs.getString("id");
                if (tmp_id == null) continue;
                if (!JavaTools.isLong(tmp_id)) continue;
                re.add(PermissionGroup.get(Long.valueOf(tmp_id)));
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
        return re;
    }
    public static List<Long> groupIds() {
        List<Long> re = new ArrayList<>();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksPermissionGroupTable + ";");
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
    public static List<String> groupNames() {
        List<String> re = new ArrayList<>();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + ranksPermissionGroupTable + ";");
            while (rs.next()) {
                String dpN = rs.getString("name");
                if (dpN == null) continue;
                re.add(dpN);
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
        return re;
    }

    public static PermissionGroup getDefaultGroup() {
        if (!groupNames().contains(DefaultConfiguration.defaultPermissionGroup.get())) {
            return newGroup(DefaultConfiguration.defaultPermissionGroup.get(), new ArrayList<>());
        } else {
            return PermissionGroup.get(DefaultConfiguration.defaultPermissionGroup.get());
        }
    }

    public static void init() {
        try {
            getDefaultGroup();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
