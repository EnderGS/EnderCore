package me.ender.core.ability;

import java.util.*;

public class CooldownManager {
    public static final Set<UUID> Exempt = new HashSet<>();
    private Map<UUID, Long> cooldowns; //or could use a Map<UUID, Map<AbilityEnum, Long>
    public void setCooldown(UUID player, Long time) {
        if(Exempt.contains(player))
            return;
        if (time == null)
            cooldowns.remove(player);
        else
            cooldowns.put(player, time);
    }

    public long getCooldown(UUID player) {
        if(Exempt.contains(player))
            Exempt.remove(player);
        var p = cooldowns.get(player);
        return (p == null ? 0L : p);
    }

    public CooldownManager() {
        cooldowns = new HashMap<>();
    }

}
