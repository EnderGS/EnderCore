package me.ender.core.ability;

import com.google.inject.Inject;
import io.papermc.paper.enchantments.EnchantmentRarity;
import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class VanishAbility extends CustomEnchant implements Listener {
    private CooldownManager cooldowns;
    private long defaultCooldown = 5; //600; //get from config
    private int defaultLength;
    public static VanishAbility INSTANCE; //wish it could be final
    private final Core plugin;
    @Inject
    public VanishAbility(Core plugin, CooldownManager cooldowns) {
        super(plugin, "Vanish");
        this.plugin = plugin;
        this.cooldowns = cooldowns;
        this.defaultCooldown = plugin.config.getInt("abilities.vanish.cooldown");
        this.defaultLength = plugin.config.getInt("abilities.vanish.length");
        if(defaultCooldown < defaultLength) plugin.getLogger().info("The cooldown for VanishAbility is shorter than the duration of the length, this may cause problems");
        INSTANCE = this;
    }
    //region Enchantment
    @Override
    public @NotNull String getName() {
        return this.name;
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
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return Set.of(EquipmentSlot.CHEST);
    }
    //endregion
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        if(!(e.getPlayer() instanceof Player)) return;
        if(!e.isSneaking()) return;
        var p = e.getPlayer();
        if(p.getInventory().getChestplate().containsEnchantment(INSTANCE)) {
            var time =System.currentTimeMillis();
            var timeLeft=  time - cooldowns.getCooldown(p.getUniqueId());
            if (TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= defaultCooldown) {
                p.sendMessage(ChatColor.RED.toString() + TimeUnit.MILLISECONDS.toSeconds(timeLeft) + "used");
                p.setSilent(true);
                p.setInvisible(true);
                cooldowns.setCooldown(p.getUniqueId(), time);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    p.setSilent(false);
                    p.setInvisible(false);
                }, defaultLength * 20);
            } else {
                p.sendMessage(ChatColor.RED.toString() + TimeUnit.MILLISECONDS.toSeconds(timeLeft) + "seconds before you can use this feature again.");
            }
        }
    }
}
