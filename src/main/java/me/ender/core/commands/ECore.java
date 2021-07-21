package me.ender.core.commands;

import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.ability.CooldownManager;
import me.ender.core.events.PluginReloadEvent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ECore extends ECommand{

    private final List<String> completes = List.of("reload", "cooldown");


    @Inject
    public ECore(Core plugin) {
        super(plugin, "ecore");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage("Must provide another argument");
            return false;
        } else {
            switch (args[0]) {
                case "reload":
                    plugin.reloadConfig();
                    Bukkit.getPluginManager().callEvent(new PluginReloadEvent(plugin, sender));
                    break;
                case "cooldown":
                    if(args.length <2)
                        return false;
                    //okay this can edit the config from within minecraft
                    //
//                    switch(args[1]) {
//                        case "toggle":
//                            //toggle can have player target, but if no target, than executing player
//                            Player target = null;
//                            if(args.length ==3) //ecore cooldown toggle EnderGS
//                                target = getPlayerTarget(args[2]);
//                            else if(sender instanceof Player)
//                                target = (Player) sender;
//                            if(CooldownManager.Exempt.contains(target.getUniqueId())) {
//                                CooldownManager.Exempt.remove(target.getUniqueId());
//                                sender.sendMessage(target.getName() + " removed from cooldown exemption");
//                            }
//                            else { //add player to exempt
//                                CooldownManager.Exempt.add(target.getUniqueId());
//                                sender.sendMessage(target.getName() + " added from cooldown exemption");
//                            }
//
//                    }
                    break;
            }
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return completes;
    }
}
