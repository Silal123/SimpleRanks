package simpleranks.utils;

import simpleranks.Simpleranks;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {

    private static String databasePath = Simpleranks.instance.getDataFolder().getAbsolutePath() + File.separator + "database.db";
    private static String databaseUrl = "jdbc:sqlite://" + databasePath;
    private static String databaseUsername = "";
    private static String databasePassword = "";

    public static Connection databaseConnection;
    public static Statement database;

    public static String playerDataTable = "simpleranks_playerdata";
    public static String ranksDataTable = "simpleranks_ranks";
    public static String ranksPermissionGroupTable = "simpleranks_permission_groups";

    public static void init() {
        initFiles();

        try {
            databaseConnection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
        } catch (Exception e) { System.err.println("[DATENBANK] fehler bei der Verbindung mit der Datenbank!"); }

        try {
            database = databaseConnection.createStatement();
        } catch (Exception e) { System.err.println("[DATENBANK] fehler beim erstellen des Statement!"); }

        try {
            database.executeUpdate("CREATE TABLE IF NOT EXISTS \"simpleranks_ranks\" (\n" +
                    "\t\"id\"\tTEXT NOT NULL,\n" +
                    "\t\"displayName\"\tTEXT NOT NULL,\n" +
                    "\t\"shortName\"\tTEXT,\n" +
                    "\t\"color\"\tTEXT NOT NULL,\n" +
                    "\t\"position\"\tINTEGER NOT NULL\n" +
                    ");");

            if (!databaseConnection.getMetaData().getColumns(null, null, "simpleranks_ranks", "group").next()) {
                database.execute("ALTER TABLE \"simpleranks_ranks\" ADD \t\"group\"\t TEXT;");
            }

            database.executeUpdate("CREATE TABLE IF NOT EXISTS \"simpleranks_permission_groups\" (\n" +
                    "\t\"id\"\tTEXT NOT NULL,\n" +
                    "\t\"name\"\tTEXT NOT NULL,\n" +
                    "\t\"permissions\"\tTEXT NOT NULL\n" +
                    ");");

            database.executeUpdate("CREATE TABLE IF NOT EXISTS \"simpleranks_playerdata\" (\n" +
                    "\t\"uuid\"\tTEXT NOT NULL,\n" +
                    "\t\"rank\"\tTEXT,\n" +
                    "\t\"timer\"\tTEXT DEFAULT -1\n" +
                    ");");
        } catch (Exception e) { System.err.println("[DATENBANK] fehler beim erstellen der Tabellen!"); e.printStackTrace(); }
    }

    private static void initFiles() {
        File database = new File(databasePath);
        try {
            if (!database.exists()) { database.createNewFile(); }
        } catch (Exception e) { System.err.println("[DATABASE] fehler beim Erstellen des Datenbank files '" + databasePath + "'!"); }
    }

    public static void shutdown() {
        try {
            if (databaseConnection != null) databaseConnection.close();
            if (database != null) database.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

}
