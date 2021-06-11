package me.ender.core.events;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
public class MobSpawnEvent {
    private final FileConfiguration config;

    @Inject
    public MobSpawnEvent(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if(config.getBoolean("spawn-mobs"))
            Bukkit.getLogger().info("TEST");
            if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) event.setCancelled(true);
    }
}
