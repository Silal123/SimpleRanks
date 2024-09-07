package simpleranks.utils.config;

import simpleranks.Simpleranks;
import simpleranks.utils.JsonManager;
import simpleranks.utils.config.ConfigValue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class DefaultConfiguration {
    public static File configFile = new File(Simpleranks.data + File.separator + "config.json");

    public static ConfigValue<String> defaultRank = new ConfigValue<>("defaultRank", String.class, configFile.toPath(), "Spieler");

    public static ConfigValue<String> chatRankFormat = new ConfigValue<>("rank.chatFormat", String.class, configFile.toPath(), "{rank_color}{rank_dpname} &8»&7 {player_name}&8:&7 {message}");
    public static ConfigValue<Boolean> chatRankEnabled = new ConfigValue<>("rank.chatRank", Boolean.class, configFile.toPath(), true);

    public static ConfigValue<String> teamRankSeparator = new ConfigValue<>("rank.teamSeparator", String.class, configFile.toPath(), "»");
    public static ConfigValue<Boolean> teamRankEnabled = new ConfigValue<>("rank.teamRank", Boolean.class, configFile.toPath(), true);
    public static ConfigValue<String> teamRankPlayerNameColor = new ConfigValue<>("rank.teamRankPlayerNameColor", String.class, configFile.toPath(), "7");

    public static ConfigValue<Boolean> rankTimerEnabled = new ConfigValue<>("rankTimer", Boolean.class, configFile.toPath(), true);

    public static ConfigValue<String> defaultPermissionGroup = new ConfigValue<>("defaultGroup", String.class, configFile.toPath(), "Default");


    public static void init() {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                JsonManager defaultJson = new JsonManager();

                defaultJson.addProperty("defaultRank", "Player");
                defaultJson.addProperty("defaultGroup", "Default");
                defaultJson.addProperty("rankTimer", true);
                defaultJson.addProperty("rank", new JsonManager()
                        .addProperty("chatFormat", "{rank_color}{rank_dpname} &8»&7 {player_name}&8:&7 {message}")
                        .addProperty("chatRank", true)
                        .addProperty("teamSeparator", "»")
                        .addProperty("teamRank", true)
                        .addProperty("teamRankPlayerNameColor", "7")
                );

                try {
                    Files.writeString(configFile.toPath(), JsonManager.makePrettier(defaultJson.toJsonString()), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) { throw new RuntimeException(e); }
            } catch (IOException e) { throw new RuntimeException(e); }
        }
    }

}
