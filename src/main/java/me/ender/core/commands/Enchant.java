package me.ender.core.commands;

import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.CustomEnchant;
import me.ender.core.Util;
import me.ender.core.ability.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Enchant implements CommandExecutor, TabCompleter {

    private final Core plugin;
    public final List<String> enchants = List.of("executioner", "frostbite", "necromancer", "sharp", "vanish");

    @Inject
    public Enchant(Core plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!Util.isPlayer(sender)) return false;
        var p = (Player)sender;
        var item = p.getInventory().getItemInMainHand();
        if(item.getType().equals(Material.AIR)) {
            p.sendMessage("You can not enchant an empty hand");
            return false;
        }
        //if(sender.hasPermission("ender.core.enchant.unsafe")) {
            try {
                switch(args[0].toLowerCase()) {
                    case "executioner":
                       CustomEnchant.enchantItem(item, ExecutionerAbility.INSTANCE, Integer.parseInt(args[1]));
                        break;
                    case "frostbite":
                        CustomEnchant.enchantItem(item, FrostbiteAbility.INSTANCE, Integer.parseInt(args[1]));
                        break;
                    case "necromancer":
                        CustomEnchant.enchantItem(item, NecromancerAbility.INSTANCE, Integer.parseInt(args[1]));
                        break;
                    case "sharp":
                        CustomEnchant.enchantItem(item, SharpAbility.INSTANCE, Integer.parseInt(args[1]));
                        break;
                    case "vanish":
                        CustomEnchant.enchantItem(item, VanishAbility.INSTANCE, Integer.parseInt(args[1]));
                        break;
                }
                //item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.fromString(args[0], plugin)), Integer.parseInt(args[1]));
            } catch (Exception e) {
                p.sendMessage(e.getMessage());
                return false;
            //}
//        } else {
//
//            try {
//                item.addEnchantment(Enchantment.getByKey(NamespacedKey.fromString(args[0])), Integer.parseInt(args[1]));
//            } catch (Exception e) {
//                p.sendMessage("You do not have permission to do unsafe enchants");
//                return false;
//            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return enchants;
    }
}
