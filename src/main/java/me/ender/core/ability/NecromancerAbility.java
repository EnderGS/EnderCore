package me.ender.core.ability;


import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import me.ender.core.IResettable;
import me.ender.core.events.PluginReloadEvent;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class NecromancerAbility extends CustomEnchant implements Listener, IResettable {
    private CooldownManager cooldowns;
    private long cooldown = 600; //get from config in seconds

    public static NecromancerAbility INSTANCE; //wish it could be final

    @Inject
    public NecromancerAbility(Core plugin, CooldownManager cooldowns) {
        super(plugin, "Necromancer");
        this.cooldowns = cooldowns;
        this.cooldown = plugin.getConfig().getLong("abilities.necromancer.cooldown");
        INSTANCE = this;
    }
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
            if (TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= cooldown) {
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
//    @EventHandler
//    public void onPlayerInteract(PlayerInteractEvent event) {
//        Player p = event.getPlayer();
//        if (!(event.getAction() == Action.RIGHT_CLICK_AIR)) return;
//        //check cooldown of item;
//        var item = p.getInventory().getItemInMainHand();
//        if(item.containsEnchantment(INSTANCE)) {
//            var timeLeft= System.currentTimeMillis() - cooldowns.getCooldown(p.getUniqueId());
//            if (TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= defaultCooldown) {
//                //ready to use
//                cooldowns.setCooldown(p.getUniqueId(), System.currentTimeMillis());
//            } else {
//                p.sendMessage(ChatColor.RED.toString() + TimeUnit.MILLISECONDS.toSeconds(timeLeft) + "seconds before you can use this feature again.");
//            }
//
//        }
//    }

    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldowns.setCooldown(event.getPlayer().getUniqueId(), null); //remove player
    }

    @Override
    @EventHandler
    public void onReset(PluginReloadEvent e) {
        var config = e.getPlugin().getConfig();
        this.cooldown = config.getLong("abilities.necromancer.cooldown");

    }
}
