package simpleranks.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import simpleranks.system.ChatInput;
import simpleranks.system.ScoreboardSystem;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (ChatInput.handleChatMessage(e)) return;
        ScoreboardSystem.playerChatEvent(e);
    }
}
