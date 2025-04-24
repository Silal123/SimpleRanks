package simpleranks.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import simpleranks.system.rankgui.manager.RankManagerGui;
import simpleranks.utils.Gui;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        if (Gui.activeGuis.containsKey(p.getUniqueId())) {
            Gui gui = Gui.activeGuis.get(p.getUniqueId());

            if (gui instanceof RankManagerGui) {
                RankManagerGui.handleInventoryEvents(e);
            }
        }

    }

}
