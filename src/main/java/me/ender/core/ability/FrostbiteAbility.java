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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;


public class FrostbiteAbility extends CustomEnchant implements Listener {

    public static FrostbiteAbility INSTANCE; //wish it could be final

    @Inject
    public FrostbiteAbility(Core plugin) {
        super(plugin, "Frostbite");
        INSTANCE = this;
    }
    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return Set.of(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent e) {
        if(!e.getBow().containsEnchantment(INSTANCE)) return;
        if(!(e.getProjectile() instanceof Arrow)) return;
        //if want to make ice block, then add persistent data or add to hashmap
        var arrow = (Arrow)e.getProjectile();
        //apply for 3 seconds. move to config.
        arrow.addCustomEffect(PotionEffectType.SLOW.createEffect(60, 255), true);
    }
}
