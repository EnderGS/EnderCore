package me.ender.core;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.ender.core.ability.FrostbiteAbility;
import me.ender.core.ability.NecromancerAbility;
import me.ender.core.commands.*;
import me.ender.core.events.DamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.stream.Stream;

@Singleton
public class Core extends JavaPlugin {
    public HashMap<String,CustomItem> customItems;

    public Path customItemPath;
    private Injector injector;

    @Inject private Home home;
    @Inject private ECustom eCustom;
    @Inject private Enchant cEnchant;

    @Inject private DamageEvent DamageEvent;
    @Inject private NecromancerAbility necromancerAbility;
    @Inject private FrostbiteAbility frostbiteAbility;

    @Override
    public void onEnable() {
        setupCustomItems();
        Binder module = new Binder(this);
        injector = module.createInjector();
        injector.injectMembers(this);
        injector.injectMembers(this.getConfig());
        var ecore = getCommand("ecore");
        ecore.setExecutor(new ECore());
        var enchant = getCommand("enchant");
        enchant.setExecutor(cEnchant);
        getCommand("espawn").setExecutor(new Spawn());
        getCommand("home").setExecutor(this.home);
        getCommand("ecustom").setExecutor(this.eCustom);
        Bukkit.getPluginManager().registerEvents(this.necromancerAbility, this);
        Bukkit.getPluginManager().registerEvents(this.frostbiteAbility, this);
        Bukkit.getPluginManager().registerEvents(this.DamageEvent, this);
        register();
    }
    public void register() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            EnchantmentWrapper.registerEnchantment(frostbiteAbility);
            EnchantmentWrapper.registerEnchantment(necromancerAbility);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupCustomItems() {
        customItems = new HashMap<>(); //convert to ItemStack
        customItemPath = getDataFolder().toPath().resolve("custom-items");
        try {
            Gson gson = new Gson();
            if (Files.notExists(customItemPath)) {
                Files.createDirectories(customItemPath);
            }
            try (Stream<Path> paths = Files.walk(customItemPath)) {
                paths
                        .filter(Files::isRegularFile)
                        .forEach(f -> {
                            try {
                                var parsed = gson.fromJson(new FileReader(f.toFile()), CustomItem.class);
                                customItems.put(parsed.name, parsed);
                            } catch (FileNotFoundException e) {
                                getLogger().log(Level.SEVERE, "Failed to load {0}", f.toString());
                                e.printStackTrace();
                            }
                        });
            }

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to load custom items from json", e);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("FUCK");
    }
}
