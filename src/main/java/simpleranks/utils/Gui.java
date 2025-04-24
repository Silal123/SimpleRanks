package simpleranks.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import simpleranks.system.ScoreboardSystem;
import simpleranks.system.rankgui.manager.RankManagerGui;

import java.util.*;

public abstract class Gui {

    public static final Map<UUID, Gui> activeGuis = new HashMap<>();
    protected final Player owner;
    protected Inventory activeInventory;
    private int previousInventorySize;

    public Gui(Player owner) {
        this.owner = owner;
        activeGuis.put(owner.getUniqueId(), this);
    }

    public static void handleInventoryClose(InventoryCloseEvent e) {
        UUID key = e.getPlayer().getUniqueId();
        if (!activeGuis.containsKey(key)) return;
        Gui activeGui = activeGuis.get(key);
        int inventorySize = e.getInventory().getSize();

        if (activeGui instanceof RankManagerGui g) {
            ScoreboardSystem.reloadAll();
        }

        if (activeGui.activeInventory.getSize() != inventorySize) return;
        activeGuis.remove(key);
    }

    public Inventory createInventory(int size) {
        if (activeInventory != null) previousInventorySize = activeInventory.getSize();

        if (activeInventory == null || activeInventory.getSize() != size) {
            activeInventory = Bukkit.createInventory(owner, size, getTitle());
            return activeInventory;
        }

        activeInventory.clear();
        return activeInventory;
    }

    public void setVerticalItems(Inventory i, ItemStack itemStack, int star, int stop) {
        for (int slot = star; slot > stop; slot++) {
            i.setItem(slot, itemStack);
        }
    }

    public void updateInventory() {
        if (activeInventory == null) return;

        if (activeInventory.getSize() != previousInventorySize) {
            owner.closeInventory();
            owner.openInventory(activeInventory);
            activeGuis.put(owner.getUniqueId(), this);
        }
    }

    public void close() {
        owner.closeInventory();
    }

    public static <T extends Gui> void reloadAllInventoriesOfType(Class<T> type) {
        for (Gui gui : activeGuis.values().stream().filter(x -> x.getClass().equals(type)).toList()) {
            gui.reload();
        }
    }

    public abstract void reload();

    protected static <T extends Gui> Optional<T> getGuiForPlayer(UUID playerUuid, Class<T> type) {
        Gui activeGui = activeGuis.get(playerUuid);
        if (activeGui == null) return Optional.empty();
        if (!Objects.equals(activeGui.getClass(), type)) return Optional.empty();
        return Optional.of((T)activeGui);
    }

    public abstract String getTitle();

}
