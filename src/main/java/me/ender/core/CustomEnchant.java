package me.ender.core;

import java.util.List;

import javax.management.timer.Timer;

import com.google.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public abstract class CustomEnchant extends Enchantment {
    //need a class and a manager
    //best way to do it is through lore and probably DataContainer in item and show through lore.
    //
    public NamespacedKey key;
    public String name;
    private Component lore;
    //Does not support level for now
    @Inject
    public CustomEnchant(Core plugin, String name) {
        super(new NamespacedKey(plugin, name.toLowerCase()));
        this.name = name;
        this.lore = Component.text(ChatColor.GRAY + this.name + " I"); //no level displayed, secret level
    }
    public static boolean hasEnchant(ItemStack item, CustomEnchant enchant) {
        var container = item.getItemMeta().getPersistentDataContainer();
        return container.has(enchant.key, PersistentDataType.STRING);
    }

    public static void enchantItem(ItemStack item, Enchantment enchant, int level) {
        item.addUnsafeEnchantment(enchant, level);
        if(enchant instanceof CustomEnchant)
        addLore(item, ((CustomEnchant)enchant).lore); //right now only support level I
    }
    public static void addLore(ItemStack item, Component lore) {
        var l = item.lore();
        if (l == null) {
            item.lore(List.of(lore));
        } else {
            //add to the top of the lore
            item.lore().add(0, lore);
        }
        //do i need to redo the lore?
    }
    public static void removeLore(ItemStack item, CustomEnchant enchant) {
        var l = item.lore();
        if (l == null) {
            Bukkit.getLogger().info("Tried to remove lore where ");
            return;
        } else {
            l.remove(enchant.lore);
        }
    }
    public static void removeEnchant(ItemStack item, CustomEnchant enchant) {
        item.removeEnchantment(enchant);
        removeLore(item, enchant);
    }
}