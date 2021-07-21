package me.ender.core.ability;

import com.google.inject.Inject;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import me.ender.core.IResettable;
import me.ender.core.events.PluginReloadEvent;
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


public class FrostbiteAbility extends CustomEnchant implements Listener, IResettable {

    public static FrostbiteAbility INSTANCE; //wish it could be final
    private int duration;
    @Inject
    public FrostbiteAbility(Core plugin) {
        super(plugin, "Frostbite");
        INSTANCE = this;
        duration = plugin.config.getInt("abilities.frostbite.duration") * 20; //to ticks
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
        arrow.addCustomEffect(PotionEffectType.SLOW.createEffect(duration, 255), true);
    }

    @Override
    @EventHandler
    public void onReset(PluginReloadEvent e) {
        duration = e.getPlugin().config.getInt("abilities.frostbite.duration") * 20; //to ticks
    }
}
