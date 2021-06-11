package me.ender.core.ability;

import com.google.inject.Inject;
import io.papermc.paper.enchantments.EnchantmentRarity;
import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Set;

public class SharpAbility extends CustomEnchant implements Listener {
    public static SharpAbility INSTANCE; //wish it could be final

    @Inject
    public SharpAbility(Core plugin) {
        super(plugin, "Sharp");
        INSTANCE = this;
    }
    @Override
    public @NotNull String getName() {
        // TODO Auto-generated method stub
        return this.name;
    }

    @Override
    public int getMaxLevel() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
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

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return true;
    }

    @Override
    public @NotNull Component displayName(int level) {
        return Component.text(ChatColor.GRAY + name);
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
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return null;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player)) return;
        if(!(e.getEntity() instanceof LivingEntity)) return;
        var p = (Player)e.getDamager();
        if(!p.getInventory().getItemInMainHand().containsEnchantment(INSTANCE)) return;
        var le = (LivingEntity)e.getEntity();
        e.setDamage(0.0);
        le.setHealth(Math.max(0,le.getHealth()-6));

    }
}
