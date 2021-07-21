package me.ender.core;

import me.ender.core.Core;
import me.ender.core.events.PluginReloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public interface IResettable extends Listener {
    void onReset(PluginReloadEvent e);
}
