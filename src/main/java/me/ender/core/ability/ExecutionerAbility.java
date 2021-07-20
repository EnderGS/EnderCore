package me.ender.core.ability;

import com.google.inject.Inject;
import io.papermc.paper.enchantments.EnchantmentRarity;
import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Set;

public class ExecutionerAbility extends CustomEnchant implements Listener {
    public static ExecutionerAbility INSTANCE;
    @Inject
    public ExecutionerAbility(Core plugin) {
        super(plugin, "Executioner");
        INSTANCE = this;
    }
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
        return null;
    }
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if(e.getEntity().getKiller() ==null) return;
        Player p = e.getEntity().getKiller();
        if(!p.getInventory().getItemInMainHand().containsEnchantment(INSTANCE)) return;
        //there is a bug, that is intentional for now, that the mob might drop 2 skulls because
        //all that this does currently is add a skull to the drop list.
        //var item = new ItemStack()
        switch(e.getEntity().getType()) {
            case PLAYER:
                var pSkull = new ItemStack(Material.PLAYER_HEAD);
//                if(!pSkull.hasItemMeta()) {
//                    var meta = Item
//
//                }
                var meta = (SkullMeta)pSkull.getItemMeta();
                meta.setPlayerProfile(((Player)e.getEntity()).getPlayerProfile());
                e.getDrops().add(pSkull);
                break;
            case SKELETON:
                e.getDrops().add(new ItemStack(Material.SKELETON_SKULL, 1));
                break;
            case WITHER_SKELETON:
                e.getDrops().add(new ItemStack(Material.WITHER_SKELETON_SKULL, 1));
                break;
            case CREEPER:
                e.getDrops().add(new ItemStack(Material.CREEPER_HEAD, 1));
                break;
            case ZOMBIE:
                e.getDrops().add(new ItemStack(Material.ZOMBIE_HEAD, 1));
                break;
        }
    }


}
