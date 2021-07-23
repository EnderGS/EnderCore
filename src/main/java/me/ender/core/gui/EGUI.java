package me.ender.core.gui;

import me.ender.core.Core;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class EGUI implements IGUI, Listener{
    protected Inventory inventory;
    protected final Core plugin;

    public EGUI(Core plugin, String title, int size) {
        this.plugin = plugin;
        inventory = Bukkit.createInventory(null, size, Component.text(title));
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    //Prevent taking items from invetory

    public void openInventory(HumanEntity e) {
        e.openInventory(inventory);
    }
}
