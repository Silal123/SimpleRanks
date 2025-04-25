package simpleranks.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import simpleranks.utils.Group;
import simpleranks.utils.Rank;
import simpleranks.utils.config.PlayerConfiguration;

import java.util.UUID;

public interface RankAPI {

    PlayerConfiguration getPlayer(Player p);
    PlayerConfiguration getUUID(UUID uuid);
    PlayerConfiguration getOfflinePlayer(OfflinePlayer p);

    Rank getRankByName(String name);
    Rank getRankById(long id);
    Rank getRankByPosition(int position);

    boolean isRankExistent(String name);
    boolean isRankIdExistent(long id);

    Rank getDefaultRank();

    Group getGroupByName(String name);
    Group getGroupById(long id);

    boolean isGroupExistent(String name);
    boolean isGroupIdExistent(long id);

    ConfigManager getConfig();
}
