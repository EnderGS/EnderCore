package me.ender.core.ability;

import com.google.inject.Inject;

import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;


public class FrostbiteAbility extends CustomEnchant implements Listener {

    public static FrostbiteAbility INSTANCE; //wish it could be final

    @Inject
    public FrostbiteAbility(Core plugin) {
        super(plugin, "Frostbite");
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
    @EventHandler
    public void onShootBow(EntityShootBowEvent e) {
        if(!e.getBow().containsEnchantment(INSTANCE)) return;
        if(!(e.getProjectile() instanceof Arrow)) return;
        //if want to make ice block, then add persistent data or add to hashmap
        var arrow = (Arrow)e.getProjectile();
        //apply for 3 seconds. move to config.
        arrow.addCustomEffect(PotionEffectType.SLOW.createEffect(60, 255), true);
        arrow.addCustomEffect(PotionEffectType.JUMP.createEffect(60, 100000), true);
    }
}
