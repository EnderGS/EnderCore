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

public class Enchant extends ECommand {

    public final List<String> enchants = List.of("executioner", "frostbite", "necromancer", "sharp", "vanish");

    @Inject
    public Enchant(Core plugin) {
        super(plugin, "eenchant");
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!Util.isPlayer(sender)) return false;
        var p = (Player) sender;
        if (args.length < 1) {
            p.sendMessage("Not enough arguments");
            return false;
        }
        var item = p.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            p.sendMessage("You can not enchant an empty hand");
            return false;
        }
        int level = 1;
        if (args.length == 2) {
            try {
                Integer.parseInt(args[1]);
            } catch (Exception e) {
                p.sendMessage(e.getMessage());
                return false;
            }
        }
             switch(args[0].toLowerCase()) {
        case "executioner":
            CustomEnchant.enchantItem(item, ExecutionerAbility.INSTANCE, level);
            break;
        case "frostbite":
            CustomEnchant.enchantItem(item, FrostbiteAbility.INSTANCE, level);
            break;
        case "necromancer":
            CustomEnchant.enchantItem(item, NecromancerAbility.INSTANCE, level);
            break;
        case "sharp":
            CustomEnchant.enchantItem(item, SharpAbility.INSTANCE, level);
            break;
        case "vanish":
            CustomEnchant.enchantItem(item, VanishAbility.INSTANCE, level);
            break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return enchants;
    }
}
