package simpleranks.api;

import org.bukkit.Bukkit;
import simpleranks.SimpleRanks;

public class SimpleAPI {

    public static RankAPI getApi() {
        if (!Bukkit.getPluginManager().isPluginEnabled("SimpleRanks")) return null;
        return ((SimpleRanks) Bukkit.getPluginManager().getPlugin("SimpleRanks")).getApi();
    }

}
