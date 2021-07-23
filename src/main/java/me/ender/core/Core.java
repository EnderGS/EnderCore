package me.ender.core;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.ender.core.ability.*;
import me.ender.core.commands.*;
import me.ender.core.gui.RecipeGUI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

public class Core extends JavaPlugin {
    public HashMap<String, me.ender.core.CustomItems> customItems;

    public final String customItemPath = "plugins/EnderCore/custom-items";
    public final NamespacedKey itemName = new NamespacedKey(this, "customName");
    public FileConfiguration config;
    private Injector injector;

    @Inject private Enchant cEnchant;
    @Inject private ECore eCore;
    @Inject private ECustom eCustom;

    @Inject private GUI eGUI;
    public RecipeGUI recipeGUI;

    public Map<NamespacedKey, Recipe> CustomItems = new HashMap<>();


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
        registerGUIs();
    }

    private void registerGUIs() {
        this.recipeGUI = new RecipeGUI(this);
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
