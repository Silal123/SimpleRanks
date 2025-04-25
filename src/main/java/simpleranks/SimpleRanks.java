package simpleranks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import simpleranks.api.RankAPI;
import simpleranks.api.RankAPIImpl;
import simpleranks.commands.BroadcastCommand;
import simpleranks.commands.RankCommand;
import simpleranks.commands.SimpleRanksCommand;
import simpleranks.commands.tabcomplete.BroadcastCommandTabComplete;
import simpleranks.commands.tabcomplete.RankCommandTabComplete;
import simpleranks.commands.tabcomplete.SimpleRanksComandTabComplete;
import simpleranks.listeners.*;
import simpleranks.system.ScoreboardSystem;
import simpleranks.system.placeholderapi.SimpleRanksPlaceholder;
import simpleranks.utils.*;
import simpleranks.utils.config.DefaultConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class SimpleRanks extends JavaPlugin {

    private static SimpleRanks instance;
    public static SimpleRanks getInstance() {
        return instance;
    }

    private RankAPI api;
    public RankAPI getApi() {
        return this.api;
    }

    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;
        api = new RankAPIImpl(this);

        initListeners();
        initCommands();
        initFiles();
        DefaultConfiguration.init();
        //Language.init();
        Database.init();
        Rank.init();
        Group.init();
        PermissionsManager.reload();
        ScoreboardSystem.reloadAll();

        getLogger().info("Loaded all ranks: " + Rank.rankNames());
        getLogger().info("Loaded all groups: " + Group.groupNames());

        getLogger().info("Starting update check...");
        checkForUpdates();

        getLogger().info("Loading Metrics...");
        this.metrics = new Metrics(this, 25609);

        this.metrics.addCustomChart(new Metrics.SingleLineChart("rank_count", () -> {
            return Rank.rankNames().size();
        }));

        getLogger().info("Starting up...");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new SimpleRanksPlaceholder(this).register();
        }

        getLogger().info("Checking config...");

        if (DefaultConfiguration.chatRankFormat.get().contains("{") && DefaultConfiguration.chatRankFormat.get().contains("}")) {
            getLogger().info("Outdated config found!");
            getLogger().warning("You are using an outdated config format! instead of {} please use %%");
        }

        getLogger().info("Successful started Plugin version " + getDescription().getVersion() + "!");
        getLogger().info("Plugin created by - " + getDescription().getAuthors());
        getLogger().info("Join our Community - " + getDescription().getWebsite());

        Scheduler.start();
    }

    private void initListeners() {
        PluginManager man = this.getServer().getPluginManager();
        man.registerEvents(new PlayerJoinListener(), this);
        man.registerEvents(new PlayerLeaveListener(), this);
        man.registerEvents(new PlayerChatListener(), this);
        man.registerEvents(new InventoryCloseListener(), this);
        man.registerEvents(new InventoryClickListener(), this);
    }

    private void initCommands() {
        getCommand("simpleranks").setExecutor(new SimpleRanksCommand());
        getCommand("simpleranks").setTabCompleter(new SimpleRanksComandTabComplete());
        getCommand("srank").setExecutor(new RankCommand());
        getCommand("srank").setTabCompleter(new RankCommandTabComplete());
        getCommand("srbroadcast").setExecutor(new BroadcastCommand());
        getCommand("srbroadcast").setTabCompleter(new BroadcastCommandTabComplete());
    }

    @Override
    public void onDisable() {
        for (Gui gui : Gui.activeGuis.values()) {
            gui.close();
        }

        Database.shutdown();
        Scheduler.stop();
    }

    private void initFiles() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
    }

    private void checkForUpdates() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                String apiUrl = "https://api.github.com/repos/Silal123/SimpleRanks/releases/latest";
                HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JsonManager release = new JsonManager(json.toString());
                String latestVersion = release.getString("tag_name").replace("v", "");

                String currentVersion = getDescription().getVersion();

                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    getLogger().warning("There is a new version available: " + latestVersion);
                    getLogger().warning("You are using: " + currentVersion);
                } else {
                    getLogger().info("Your plugin is up to date!");
                }

            } catch (Exception e) {
                getLogger().warning("Error while checking for updates: " + e.getMessage());
            }
        });
    }

}
