package me.ender.core.commands;

import com.google.gson.Gson;
import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import me.ender.core.CustomItem;
import me.ender.core.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ECustom implements CommandExecutor {
    private final Core plugin;

    @Inject //maybe i should use a ICustomItemManager for file access
    public ECustom(Core plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        //usage /<command> option
        switch(strings[0]) {
            case "list":
                if(strings.length ==1) {
                    for (String item : plugin.customItems.keySet()) {
                        commandSender.sendMessage(item);
                    }
                    return true;
                }
                //do the thing;
                return true;
            case "enchant":
                return enchantItem(commandSender, strings);
            case "create": {
                return createItem(commandSender, strings);
            }
            case "copy":
            case "import": {
                return importItem(commandSender, strings);
            }
            case "modify": {
                CustomItem item = null;
                //maybe I can alias this to something;
                if(strings[1].equals("hand")) {
                    if(!Util.isPlayer(commandSender)) return false;
                    var p = (Player)commandSender;
                    return modifyItem(p.getInventory().getItemInMainHand(), strings);
                }else item = this.plugin.customItems.get(strings[1]);
                if(item == null) {
                    commandSender.sendMessage("Item not found");
                    return false;
                }

                if(modifyItem(item.itemStack, strings)) {
                    saveItem(item, true);
                    return true;
                }else return false;
                }
            case "give": {
                return giveItem(commandSender, strings);
            }
            case "delete":
                commandSender.sendMessage("You probably shouldn't do this from within Minecraft, please delete the file and reload");
                break;
            case "reload":
                //not implemented
                commandSender.sendMessage("Not implemented");
                return false;
            default:
                return false;
        }
        return false;
    }

    private boolean enchantItem(CommandSender commandSender, String[] strings) {
        if(!Util.isPlayer(commandSender)) return false;
        var p = (Player)commandSender;
        var item = p.getInventory().getItemInMainHand();
        CustomEnchant.enchantItem(item, Enchantment.getByKey(NamespacedKey.fromString(strings[1], plugin)), 1);
        return true;
    }

    private boolean giveItem(@NotNull CommandSender commandSender, @NotNull String @NotNull [] strings) {
        if(strings.length >= 3) {
            var p = Bukkit.getPlayer(strings[1]);
            if (p == null) {
                commandSender.sendMessage("Target must be real player");
                return false;
            }
            var item = this.plugin.customItems.get(strings[2]);
            if (item == null) {
                commandSender.sendMessage("Item does not exist");
                return false;
            }
            var is = item.itemStack;
            if(strings.length ==4) is = item.itemStack.asQuantity(Integer.parseInt(strings[3]));
            if(!p.getInventory().addItem(is).isEmpty())
                commandSender.sendMessage("Player's inventory full, item not added");

        }
        commandSender.sendMessage("Not enough args");
        return false;
    }

    private boolean importItem(@NotNull CommandSender commandSender, @NotNull String @NotNull [] strings) {
        //copy item from command sender inventory
        if(!Util.isPlayer(commandSender)) return false;
        var p = (Player) commandSender;
        var hand = p.getInventory().getItemInMainHand();
        if(hand.getType().isAir()) {
            p.sendMessage("You do not have a valid item in your hand");
            return false;
        }
        if(strings.length < 2) {
            p.sendMessage("You must name the item");
            //maybe from error sometime;
            return false;
        }
        var item = new CustomItem();
        item.name = strings[1];
        item.itemStack = hand;
        var newPath = this.plugin.customItemPath.resolve(strings[1]);
        if(!saveItem(item, newPath)) {
            commandSender.sendMessage("This item already exists");
            return false;
        }
        return true;
    }

    private boolean createItem(@NotNull CommandSender commandSender, @NotNull String @NotNull [] strings) {
        //ecustom create name, type, ...
        var item = new CustomItem();
        item.name = strings[1];
        item.itemStack = new ItemStack(Material.DIAMOND_SWORD);
        item.itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 3);
        var newPath = this.plugin.customItemPath.resolve(strings[1]);
        if (!saveItem(item, newPath)) {
            commandSender.sendMessage("This item already exists");
            return false;
        }
        commandSender.sendMessage(String.format("Successfully created %s", item.name));
        return true;
    }

    private boolean saveItem(CustomItem item, java.nio.file.Path newPath) {
        return saveItem(item, newPath, false);
    }

    private boolean saveItem(CustomItem item, boolean update) {
        return saveItem(item, plugin.customItemPath.resolve(item.name), update);
    }

    private boolean saveItem(CustomItem item, java.nio.file.Path newPath, boolean update) {
        if(!update) {
            plugin.customItems.put(item.name, item);
        }
        if(Files.exists(newPath) && !update) {
            return false;
        }
        //does this even work?
        //TODO: Check
//        new BukkitRunnable() {
//            @Override
//            public void run() {
                //need to make this thread safe. and async

                Gson gson = new Gson();

                try (FileWriter writer = new FileWriter(newPath.toFile())) {
                    gson.toJson(item.itemStack.getItemMeta(), writer);

                } catch (IOException e) {
                    e.printStackTrace();
                }
//            }
//        }.runTaskAsynchronously(this.plugin);

        return true;

    }

    private boolean modifyItem(ItemStack item, String[] strings) {
        switch(strings[2]) {
            case "set":
                switch(strings[3]) {
                    case "name":
                        //ecustom modify test name set
                        //set the display name = the 5 arg
                        item.getItemMeta().displayName(Component.text(strings[4]));
                    case "lore":
                        //ecustom modify test lore set "This is a lore fragment" <page>
                            StringBuilder sb = new StringBuilder();
                            int i = 5;
                            for (; i < strings.length; i++) {
                                sb.append(strings[i]);
                                if (strings[i].endsWith("\"")) break;
                            }

                        try {
                            var num = Integer.parseInt(strings[strings.length - 1]);
                            item.lore().set(num, Component.text(sb.toString()));
                            return true;
                        } catch (NumberFormatException e) {

                        }
                        item.lore(List.of(Component.text(strings[4])));
                        return true;
                    case "enchant":
                        Bukkit.getServer().sendMessage(Component.text("Do not use this"));
                        return true;
                }
            case "add":
                return false;
            case "remove":
        }
        return true;
    }
}
