package me.ender.core.commands;

import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Enchant implements CommandExecutor {

    private final Core plugin;

    @Inject
    public Enchant(Core plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!Util.isPlayer(sender)) return false;
        var p = (Player)sender;
        var item = p.getInventory().getItemInMainHand();
        p.chat(item.getI18NDisplayName());
        if(item.getType().equals(Material.AIR)) {
            p.sendMessage("You can not enchant an empty hand");
            return false;
        }
        if(sender.hasPermission("ender.core.enchant.unsafe")) {
            try {
                item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.fromString(args[0], plugin)), Integer.parseInt(args[1]));
            } catch (Exception e) {
                p.sendMessage(e.getMessage());
                return false;
            }
        } else {

            try {
                item.addEnchantment(Enchantment.getByKey(NamespacedKey.fromString(args[0])), Integer.parseInt(args[1]));
            } catch (Exception e) {
                p.sendMessage("You do not have permission to do unsafe enchants");
                return false;
            }
        }
        return true;
    }
}
