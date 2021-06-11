package me.ender.core.commands;

import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.Util;
import me.ender.core.misc.LocationDataType;
import me.ender.core.misc.PersistentDataTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class Home implements CommandExecutor {
    private final Core plugin;
    private final NamespacedKey HomeKey;
    @Inject
    public Home(Core plugin) {
        this.plugin = plugin;
        HomeKey = new NamespacedKey(plugin, "house");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        //for now no console
        if(!Util.isPlayer(commandSender)) return false;
        var p = (Player)commandSender;
        var d = p.getPersistentDataContainer();

        if(strings.length > 0) {
            switch(strings[0]) {
                case "set":
                    d.set(HomeKey, PersistentDataTypes.LOCATION, p.getLocation());
                    p.sendMessage("Successfully set home");
                    return true;
                default:
                    p.sendMessage("Unrecognized subcommand");
                    return false;
            }
        } else {

            if (d.has(HomeKey, PersistentDataTypes.LOCATION)) {
                var home = p.getPersistentDataContainer().get(HomeKey, PersistentDataTypes.LOCATION);
                p.teleport(home);
            } else p.sendMessage("You do not have a home set");
        }




        return true;
    }
}
