package simpleranks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import simpleranks.commands.RankCommand;
import simpleranks.commands.SimpleRanksCommand;
import simpleranks.commands.tabcomplete.RankCommandTabComplete;
import simpleranks.commands.tabcomplete.SimpleRanksComandTabComplete;
import simpleranks.listeners.*;
import simpleranks.system.ScoreboardSystem;
import simpleranks.system.placeholderapi.SimpleRanksPlaceholder;
import simpleranks.utils.*;
import simpleranks.utils.config.DefaultConfiguration;

import java.io.File;

public final class Simpleranks extends JavaPlugin {

    public static String data;
    public static Simpleranks instance;
    public Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;
        data = this.getDataFolder().getPath();

        initListeners();
        initCommands();
        initFiles();
        DefaultConfiguration.init();
        //Language.init();
        Database.init();
        PlayerRank.init();
        PermissionGroup.init();
        PermissionsManager.reload();
        ScoreboardSystem.reloadAll();

        getLogger().info("Loaded all ranks: " + PlayerRank.rankNames());
        getLogger().info("Loaded all groups: " + PermissionGroup.groupNames());

        getLogger().info("Loading Metrics...");
        this.metrics = new Metrics(this, 25609);

        this.metrics.addCustomChart(new Metrics.SingleLineChart("rank_count", () -> {
            return PlayerRank.rankNames().size();
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

    public void initListeners() {
        PluginManager man = this.getServer().getPluginManager();
        man.registerEvents(new PlayerJoinListener(), this);
        man.registerEvents(new PlayerLeaveListener(), this);
        man.registerEvents(new PlayerChatListener(), this);
        man.registerEvents(new InventoryCloseListener(), this);
        man.registerEvents(new InventoryClickListener(), this);
    }

    public void initCommands() {
        getCommand("simpleranks").setExecutor(new SimpleRanksCommand());
        getCommand("simpleranks").setTabCompleter(new SimpleRanksComandTabComplete());
        getCommand("rank").setExecutor(new RankCommand());
        getCommand("rank").setTabCompleter(new RankCommandTabComplete());
    }

    @Override
    public void onDisable() {
        for (Gui gui : Gui.activeGuis.values()) {
            gui.close();
        }

        Database.shutdown();
        Scheduler.stop();
    }

    public void initFiles() {
        if (!new File(data).exists()) {
            new File(data).mkdir();
        }
    }

}
