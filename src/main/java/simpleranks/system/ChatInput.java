package simpleranks.system;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import simpleranks.SimpleRanks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInput {

    private static Map<UUID, Consumer<String>> listening = new HashMap<>();

    public static void listenForInput(Player p, Consumer<String> callback) {
        listening.put(p.getUniqueId(), callback);
    }

    public static boolean handleChatMessage(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!listening.containsKey(p.getUniqueId())) return false;
        e.setCancelled(true);
        Consumer<String> callback = listening.get(p.getUniqueId());
        listening.remove(p.getUniqueId());
        Bukkit.getScheduler().runTask(SimpleRanks.getInstance(), () -> callback.accept(e.getMessage()));
        return true;
    }

}
