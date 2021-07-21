package me.ender.core.commands;

import me.ender.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public abstract class ECommand implements CommandExecutor, TabCompleter {
    protected final Core plugin;

    public ECommand(Core plugin, String command) {
        this.plugin = plugin;
        var ecore = plugin.getCommand(command);
        ecore.setExecutor(this);
        ecore.setTabCompleter(this);
    }

    public Player getPlayerTarget(String arg) {
        return Bukkit.getPlayer(arg);
    }
}
