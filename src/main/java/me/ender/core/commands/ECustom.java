package me.ender.core.commands;

import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ECustom extends ECommand implements Listener {
    private final List<String> COMMANDS1 = List.of("save", "give", "recipe", "delete");
    public List<NamespacedKey> COMMANDS2;
    public Map<InventoryView, ItemStack> queue;
    private final NamespacedKey itemName;
    @Inject
    public ECustom(Core plugin) {
        super(plugin, "ecustom");
        queue = new HashMap<>();
        //COMMANDS2 = plugin.CustomItems;
        itemName = plugin.itemName;

    }
    //make it so that you can combine
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!Util.isPlayer(commandSender)) return false;
        var p = (Player)commandSender;
        if(strings.length != 2) {
            p.sendMessage("Not enough arguments");
            return false;
        }
        switch(strings[0]) {
            case "save": {
                    var name = strings[1];
                    var item = p.getInventory().getItemInMainHand();
                    if(item.getType().isAir()) {
                        p.sendMessage("Air can not be a custom item");
                        return false;
                    }
                    var meta = item.getItemMeta();
                    meta.getPersistentDataContainer().set(itemName, PersistentDataType.STRING, name);
                    item.setItemMeta(meta);
                    var view = p.openWorkbench(null, true);
                    queue.put(view, item);
                } break;
            case "recipe": {
                var name = strings[1];
                var file = plugin.loadConfig(plugin.customItemPath+"/" + name, false);
                if(file == null) {
                    p.sendMessage("The specified item does not exist");
                    return false;
                }
                var item = file.getItemStack("result");
                var view = p.openWorkbench(null, true);
                queue.put(view, item);
            } break;
            case "give": {
                var name = strings[1];
                var file = plugin.loadConfig(plugin.customItemPath +"/" + name, false);
                if(file == null) {
                    p.sendMessage("The specified item does not exist");
                    return false;
                }
                var item = file.getItemStack("result");

                p.getInventory().addItem(item);
            } break;

            case "delete": {
                var name = strings[1];
                try {
                    if(Files.deleteIfExists(Path.of(plugin.customItemPath +"/" + name))) {
                        p.sendMessage(name + " deleted successfully");
                        COMMANDS2.remove(name);
                    }
                    else {
                        p.sendMessage(name + " not found");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        //create new array
        final List<String> completions = new ArrayList<>();
        if(strings.length ==1) {
            //copy matches of first argument from list (ex: if first arg is 'm' will return just 'minecraft')
            StringUtil.copyPartialMatches(strings[0], COMMANDS1, completions);
            //sort the list
            Collections.sort(completions);
        } else if(strings.length ==2) {
            //todo: if server slows this might be why, maybe cache the things but it probably doesn't matter because i will change this up
            StringUtil.copyPartialMatches(strings[1], COMMANDS2.stream().map(c-> c.getKey()).toList(), completions);
            //sort the list
            Collections.sort(completions);

        }
            return completions;
        //return null;
        //return COMMANDS;
    }
}
