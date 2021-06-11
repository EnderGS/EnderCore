package me.ender.core.ability;


import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

public class NecromancerAbility extends CustomEnchant implements Listener {
    private CooldownManager cooldowns;
    private final long defaultCooldown = 5; //600; //get from config in seconds

    public static NecromancerAbility INSTANCE; //wish it could be final

    @Inject
    public NecromancerAbility(Core plugin, CooldownManager cooldowns) {
        super(plugin, "Necromancer");
        this.cooldowns = cooldowns;
        INSTANCE = this;
    }
    //region Enchant
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

    //endregion
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent e) {
        var p = e.getPlayer();
        var item = p.getInventory().getItem(e.getNewSlot());
        if(item != null && item.containsEnchantment(INSTANCE) ||
        p.getInventory().getItemInOffHand().containsEnchantment(INSTANCE)) {
            p.addPotionEffect(PotionEffectType.SLOW.createEffect(Integer.MAX_VALUE, 0));
        } else {
            //get previous item and see if it was staff
            var i = p.getInventory().getItem(e.getPreviousSlot());
            if(i != null && i.containsEnchantment(INSTANCE)) {
                p.removePotionEffect(PotionEffectType.SLOW);
                //bug because will remove normal slow effect
            }
        }
    }

    @EventHandler 
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player)) return;
        if(!(e.getEntity() instanceof LivingEntity)) return;
        var p = (Player)e.getDamager();
        var t = (LivingEntity) e.getEntity();
        if(p.getInventory().getItemInMainHand().containsEnchantment(INSTANCE) ||
        p.getInventory().getItemInOffHand().containsEnchantment(INSTANCE)) {

            var time =System.currentTimeMillis();
            var timeLeft=  time - cooldowns.getCooldown(p.getUniqueId());
            if (TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= defaultCooldown) {
                p.sendMessage(ChatColor.RED.toString() + TimeUnit.MILLISECONDS.toSeconds(timeLeft) + "used");
                var num = RandomUtils.nextInt(4) + 1; //not sure if fast
                var world  =e.getEntity().getWorld();
                var loc = e.getEntity().getLocation().add(1, 0, 0);
                for(int i =0; i<num; i++) {

                    var entity = (Monster)world.spawnEntity(loc, num % 3 ==0 ? EntityType.SKELETON : EntityType.ZOMBIE);
                    entity.setTarget(t);
                }
                cooldowns.setCooldown(p.getUniqueId(), time);
            } else {
                p.sendMessage(ChatColor.RED.toString() + TimeUnit.MILLISECONDS.toSeconds(timeLeft) + "seconds before you can use this feature again.");
            }
        }
    }
    @EventHandler 
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        if(!(e.getTarget() instanceof Player)) return;
        if(!(e.getEntity() instanceof Monster)) return; //not passive mobs
        var p = (Player)e.getTarget();
        if(p.getInventory().getItemInMainHand().containsEnchantment(INSTANCE) ||
        p.getInventory().getItemInOffHand().containsEnchantment(INSTANCE)) {
            e.setCancelled(true); //cancel the target 
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        //check cooldown of item;
        var item = p.getInventory().getItemInMainHand();
        if(item.containsEnchantment(INSTANCE)) {
            var timeLeft= System.currentTimeMillis() - cooldowns.getCooldown(p.getUniqueId());
            if (TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= defaultCooldown) {
                //ready to use
                cooldowns.setCooldown(p.getUniqueId(), System.currentTimeMillis());
            } else {
                p.sendMessage(ChatColor.RED.toString() + TimeUnit.MILLISECONDS.toSeconds(timeLeft) + "seconds before you can use this feature again.");
            }

        }
    }

    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldowns.setCooldown(event.getPlayer().getUniqueId(), null); //remove player
    }
}
