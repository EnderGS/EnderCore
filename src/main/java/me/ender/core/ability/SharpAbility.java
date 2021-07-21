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
