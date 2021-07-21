package me.ender.core.ability;

import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.event.entity.EntityDeathEvent;

public class ExecutionerAbility extends CustomEnchant implements Listener {
    public static ExecutionerAbility INSTANCE;
    @Inject
    public ExecutionerAbility(Core plugin) {
        super(plugin, "Executioner");
        INSTANCE = this;
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
