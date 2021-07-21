package me.ender.core;

import java.util.List;
import java.util.Set;

import javax.management.timer.Timer;

import com.google.inject.Inject;

import io.papermc.paper.enchantments.EnchantmentRarity;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

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
//region bloat
    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return null;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    public @NotNull Component displayName(int i) {
        return null;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return null;
    }

    @Override
    public float getDamageIncrease(int i, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    //endregion
    public static boolean hasEnchant(ItemStack item, CustomEnchant enchant) {
        var container = item.getItemMeta().getPersistentDataContainer();
        return container.has(enchant.key, PersistentDataType.STRING);
    }

    public static void enchantItem(ItemStack item, Enchantment enchant, int level) {
        item.addUnsafeEnchantment(enchant, level);
        if(enchant instanceof CustomEnchant)
        addLore(item, ((CustomEnchant)enchant).lore); //right now only support level I
    }

    public static void enchantItem(ItemStack item, NamespacedKey name, int level) {
        var enchant = Enchantment.getByKey(name);
        item.addUnsafeEnchantment(enchant, level);
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