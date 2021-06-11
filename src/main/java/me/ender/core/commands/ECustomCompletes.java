package me.ender.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ECustomCompletes implements TabCompleter {
    private final List<String> subCommands = List.of("list", "create", "modify", "give", "delete");
    private final List<String> modifySubCommands = List.of("get", "set");
    private final List<String> modifySubSubCommands = List.of("lore", "name", "enchantments");
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 0) {
            return subCommands;
        } else {
            switch(strings[0]) {
                case "modify":
                    if(strings.length ==1)
                    return modifySubCommands;
                    else return modifySubSubCommands;
                default:
                    return null;
            }
        }
    }
}
