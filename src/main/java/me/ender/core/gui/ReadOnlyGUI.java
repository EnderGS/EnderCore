package me.ender.core.gui;

import me.ender.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ReadOnlyGUI extends EGUI {

    public ReadOnlyGUI(Core plugin, String title, int size) {
        super(plugin, title, size);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if(e.getInventory() != inventory) return;
        e.getWhoClicked().sendMessage("STOP");
        e.setCancelled(true);
    }
//    @EventHandler
//    public void onInventoryClick(final InventoryDragEvent e) {
//        if(e.getInventory().equals(inventory)) {
//            e.getWhoClicked().sendMessage("WOW");
//            e.setCancelled(true);
//        }
//    }
}
