package simpleranks.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import simpleranks.SimpleRanks;
import simpleranks.utils.Group;
import simpleranks.utils.Rank;
import simpleranks.utils.config.PlayerConfiguration;

import java.util.UUID;

public class RankAPIImpl implements RankAPI {

    private final SimpleRanks plugin;
    private final ConfigManager configManager;

    public RankAPIImpl(SimpleRanks plugin) {
        this.plugin = plugin;
        this.configManager = new ConfigManagerImpl();
    }


    @Override
    public PlayerConfiguration getPlayer(Player p) {
        return PlayerConfiguration.getFor(p);
    }

    @Override
    public PlayerConfiguration getUUID(UUID uuid) {
        return PlayerConfiguration.getFor(uuid);
    }

    @Override
    public PlayerConfiguration getOfflinePlayer(OfflinePlayer p) {
        return PlayerConfiguration.getFor(p.getUniqueId());
    }

    @Override
    public Rank getRankByName(String name) {
        return Rank.get(name);
    }

    @Override
    public Rank getRankById(long id) {
        return Rank.get(id);
    }

    @Override
    public Rank getRankByPosition(int position) {
        return Rank.get(position);
    }

    @Override
    public boolean isRankExistent(String name) {
        return Rank.isRankExistent(name);
    }

    @Override
    public boolean isRankIdExistent(long id) {
        return Rank.isRankExistent(id);
    }

    @Override
    public Rank getDefaultRank() {
        return Rank.getDefaultRank();
    }

    @Override
    public Group getGroupByName(String name) {
        return Group.get(name);
    }

    @Override
    public Group getGroupById(long id) {
        return Group.get(id);
    }

    @Override
    public boolean isGroupExistent(String name) {
        return Group.isGroupExistent(name);
    }

    @Override
    public boolean isGroupIdExistent(long id) {
        return Group.isGroupExistent(id);
    }

    @Override
    public ConfigManager getConfig() {
        return configManager;
    }
}
