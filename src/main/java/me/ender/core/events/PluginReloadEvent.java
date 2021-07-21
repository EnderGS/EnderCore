package me.ender.core.events;

import me.ender.core.Core;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PluginReloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Core plugin;
    private CommandSender sender;
    public PluginReloadEvent(Core plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    public Core getPlugin() {
        return plugin;
    }
    public CommandSender getSender() {
        return sender;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
