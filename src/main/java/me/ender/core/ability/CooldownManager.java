package me.ender.core.ability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private Map<UUID, Long> cooldowns;
    public void setCooldown(UUID player, Long time) {
        if (time == null)
            cooldowns.remove(player);
        else
            cooldowns.put(player, time);
    }

    public long getCooldown(UUID player) {
        return (cooldowns.get(player) == null ? 0L : cooldowns.get(player));
    }

    public CooldownManager() {
        cooldowns = new HashMap<>();
    }

}
