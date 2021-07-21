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
import java.nio.file.Path;
import java.util.*;

public class ECustom extends ECommand implements Listener {
    private final List<String> COMMANDS1 = List.of("save", "give", "recipe");
    public Set<String> COMMANDS2;
    private Map<InventoryView, ItemStack> queue;
    private final NamespacedKey itemName = new NamespacedKey(plugin, "customName");
    @Inject
    public ECustom(Core plugin) {
        super(plugin, "ecustom");
        queue = new HashMap<>();
        COMMANDS2 = new HashSet<>();

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!Util.isPlayer(commandSender)) return false;
        var p = (Player)commandSender;
        switch(strings[0]) {
            case "save": {
                var name = strings[1];
                var item = p.getInventory().getItemInMainHand();
                var meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(itemName, PersistentDataType.STRING, name);
                item.setItemMeta(meta);
                var view = p.openWorkbench(null, true);
                queue.put(view, item);
            } break;
            case "recipe": {
                var name = strings[1];
                var file = plugin.loadConfig(plugin.customItemPath + name, false);
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
                var file = plugin.loadConfig(plugin.customItemPath + name, false);
                if(file == null) {
                    p.sendMessage("The specified item does not exist");
                    return false;
                }
                var item = file.getItemStack("result");

                p.getInventory().addItem(item);
            } break;
        }
        return true;
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        var item = queue.get(e.getView());
        if (item == null) return;
        var name =item.getItemMeta().getPersistentDataContainer().get(itemName, PersistentDataType.STRING);
        var key = new NamespacedKey(plugin, name);
        //remove recipe because it is re registering,
        Bukkit.removeRecipe(key);
        var p = (Player) e.getPlayer();
        //if(!p.equals((Player)e.getPlayer())) return;
        queue.remove(e.getView());
        //p.sendMessage("HELLO");
        var i = e.getInventory().getContents();
        //the first item is the result, we already have that
        Map<Character, ItemStack> map = new HashMap<>();
        char letter = 'a';
        //generate map
        for (var item1 : i) {
            if (!map.values().contains(item1) && !item1.getType().isAir()) {
                map.put(letter, item1);
                letter++;
            }
        }
        //end map

        var path = "plugins/EnderCore/custom-items/"+name;
            var file = plugin.loadConfig(path.toString(), true);
            var pattern = convertToPattern(i, map);

            var recipe = new ShapedRecipe(new NamespacedKey(plugin, name), item);
            recipe.shape(pattern);
            for(var entry : map.entrySet()) {
                recipe.setIngredient(entry.getKey(), entry.getValue());
            }
            file.set("pattern", pattern);
            file.set("map", map);
            file.set("result", item);
            Bukkit.addRecipe(recipe);
            COMMANDS2.add(name);
        try {
            file.save(new File(path));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        }

    private String[] convertToPattern(ItemStack[] items, Map<Character, ItemStack> map) {
        var strings = new String[3];
        for(int s=0; s <3;s++)
            strings[s] = "";
        for(int i =0; i<3; i++) {
            for(int j=0; j<3; j++) {
                var item = items[i*3+j+1];
                if(item.getType() == Material.AIR)
                    strings[i] += " ";
                else {
                     char letter = 'a';
                     for(var k : map.keySet()) {
                         if(map.get(k).isSimilar(item)) {
                             strings[i] += k.toString();
                             break;
                         }
                     }
                    }
                }
            }
        if(strings[0].equals("   ")) {
            return Arrays.copyOfRange(strings, 1,3);
        }
        else if(strings[2].equals("   ")) {
            return Arrays.copyOfRange(strings, 0,2);
        }

        return strings;
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
            StringUtil.copyPartialMatches(strings[1], COMMANDS2, completions);
            //sort the list
            Collections.sort(completions);
        }
            return completions;
        //return null;
        //return COMMANDS;
    }
}
