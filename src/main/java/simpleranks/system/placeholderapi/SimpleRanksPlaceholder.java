package simpleranks.system.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simpleranks.Simpleranks;
import simpleranks.utils.JavaTools;
import simpleranks.utils.config.PlayerConfiguration;

public class SimpleRanksPlaceholder extends PlaceholderExpansion {

    private final Simpleranks plugin;

    public SimpleRanksPlaceholder(Simpleranks plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sr";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Silal";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        PlayerConfiguration config = PlayerConfiguration.getFor(player);

        if (identifier.equalsIgnoreCase("rank")) {
            return config.getRank().displayName();
        }

        if (identifier.equalsIgnoreCase("rank_color") || identifier.equalsIgnoreCase("rankColor")) {
            return config.getRank().color();
        }

        if (identifier.equalsIgnoreCase("rank_timer") || identifier.equalsIgnoreCase("rankTimer")) {
            return config.getRankTimer() < 0 ? "infinite" : JavaTools.convertMinutesToDaysHoursMinutesShort(config.getRankTimer());
        }

        if (identifier.equalsIgnoreCase("rank_timer_long") || identifier.equalsIgnoreCase("rankTimerLong")) {
            return config.getRankTimer() < 0 ? "infinite" : JavaTools.convertMinutesToDaysHoursMinutes(config.getRankTimer());
        }

        if (identifier.equalsIgnoreCase("rank_timer_raw") || identifier.equalsIgnoreCase("rankTimerRaw")) {
            return String.valueOf(config.getRankTimer());
        }

        return null;
    }
}
