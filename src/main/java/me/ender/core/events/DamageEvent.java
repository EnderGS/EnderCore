package me.ender.core.events;

import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageEvent implements Listener {
    private Core plugin;

    @Inject
    public DamageEvent(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAttackEvent(EntityDamageByEntityEvent event) {
        if(Util.isPlayer(event.getDamager())) {
            var p = (Player)event.getDamager();
            p.sendMessage(String.valueOf(event.getDamage()));
        }
    }
}
