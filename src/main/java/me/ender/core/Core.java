package me.ender.core;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.ender.core.ability.*;
import me.ender.core.commands.*;
import me.ender.core.events.DamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

public class Core extends JavaPlugin {
    public HashMap<String,CustomItem> customItems;

    public final String customItemPath = "plugins/EnderCore/custom-items";
    public FileConfiguration config;
    private Injector injector;

    @Inject private Enchant cEnchant;
    @Inject private ECore eCore;
    @Inject private ECustom eCustom;

    //@Inject private DamageEvent DamageEvent;
    @Inject private NecromancerAbility necromancerAbility;
    @Inject private FrostbiteAbility frostbiteAbility;
    @Inject private VanishAbility vanishAbility;
    @Inject private SharpAbility sharpAbility;
    @Inject private ExecutionerAbility executionerAbility;

    @Override
    public void onEnable() {
        if(!Files.exists(this.getDataFolder().toPath().resolve("config.yml")))
            this.saveResource("config.yml", false);
        FileConfiguration f = new YamlConfiguration();

        config = this.getConfig();
        //setupCustomItems();
        Binder module = new Binder(this);
        injector = module.createInjector();
        injector.injectMembers(this);
        injector.injectMembers(this.getConfig());
        //var ecore = getCommand("ecore");
        registerEvents();
        register();
        registerCustomItems();
    }
    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this.necromancerAbility, this);
        Bukkit.getPluginManager().registerEvents(this.frostbiteAbility, this);
        //Bukkit.getPluginManager().registerEvents(this.DamageEvent, this);
        Bukkit.getPluginManager().registerEvents(this.vanishAbility, this);
        Bukkit.getPluginManager().registerEvents(this.sharpAbility, this);
        Bukkit.getPluginManager().registerEvents(this.executionerAbility, this);

        Bukkit.getPluginManager().registerEvents(this.eCustom, this);
    }
    public void register() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            EnchantmentWrapper.registerEnchantment(frostbiteAbility);
            EnchantmentWrapper.registerEnchantment(necromancerAbility);
            EnchantmentWrapper.registerEnchantment(vanishAbility);
            EnchantmentWrapper.registerEnchantment(sharpAbility);
            EnchantmentWrapper.registerEnchantment(executionerAbility);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerCustomItems() {
        var file = new File(customItemPath);
        for(var f : file.listFiles()) {
            var yaml = loadConfig(f.getPath(), false);
            var patterList = yaml.getStringList("pattern");
            var pattern = new String[patterList.size()];
            patterList.toArray(pattern);


            var sec = yaml.getConfigurationSection("map");
            Map<String, ItemStack> map = (Map)sec.getValues(false);

            var item = yaml.getItemStack("result");

            var recipe = new ShapedRecipe(new NamespacedKey(this, f.getName()), item);
            recipe.shape(pattern);
            for(var entry : map.entrySet()) {
                recipe.setIngredient(entry.getKey().charAt(0), entry.getValue());
            }
            Bukkit.addRecipe(recipe);
            eCustom.COMMANDS2.add(f.getName());
        }
    }

    public FileConfiguration loadConfig(String path, boolean create) {
        try {
        var customConfigFile = new File(path);
        if (!customConfigFile.exists() && create) {
            customConfigFile.createNewFile();
        }
        var customConfig= new YamlConfiguration();
            customConfig.load(customConfigFile);
            return customConfig;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling");
    }
}
