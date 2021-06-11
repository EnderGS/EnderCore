package me.ender.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

public abstract class ECommand implements CommandExecutor {
    public Player getPlayerTarget(String arg) {
        return Bukkit.getPlayer(arg);
    }
}
