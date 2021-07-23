package me.ender.core.commands;

import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GUI extends ECommand{

    @Inject
    public GUI(Core plugin) {
        super(plugin, "egui");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!Util.isPlayer(commandSender))
            return false;
        var p = (Player)commandSender;
        if(strings.length ==0) {
            plugin.recipeGUI.openInventory(p);
            return true;
        }
        switch(strings[0]) {
            case "recipes":
                if(strings.length ==1) {
                    plugin.recipeGUI.openInventory(p);
                    return true;
                }
                switch(strings[1]) {
                    case "create": {
                        if(strings.length != 3) {
                            p.sendMessage("Not enough args");
                            return false;
                        }
                        plugin.recipeGUI.createRecipe(p, strings[2]);
                    } break;
                    case "edit": {

                    } break;
                }
                break;
            case "create":
                //this is def debug
                var size = Integer.parseInt(strings[1]);
                if(size >= 9 && size <= 54 && size % 9 == 0) {
                    var i = Bukkit.createInventory(null, size);
                    p.openInventory(i);
                } else
                    return false;
                break;
            case "reload":
                plugin.recipeGUI.reload();
                p.sendMessage("Recipe GUI reloaded");
                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
