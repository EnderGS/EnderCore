package me.ender.core.gui;

import com.google.inject.Inject;
import me.ender.core.Core;
import me.ender.core.IResettable;
import me.ender.core.events.PluginReloadEvent;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeGUI extends EGUI implements IResettable {
    //wish it could be List<Recipe> but
    private static final ItemStack AIR_REPLACE = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
    private static final ItemStack FILLER = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private static final ItemStack AIR = new ItemStack(Material.AIR);
    private static final ItemStack CRAFTING_TABLE = new ItemStack(Material.CRAFTING_TABLE);
    private static final ItemStack DELETE;
    private static final ItemStack RENAME;
    private static final ItemStack CHANGE;
    private static final ItemStack BACK;
    private static final ItemStack ADMIN_PANEL;
    private static final ItemStack CONFIRM;
    private static final ItemStack CANCEL;

    static {
        CONFIRM = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        CONFIRM.editMeta(m -> m.displayName(Component.text(ChatColor.GREEN + "Confirm")));

        CANCEL = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        CANCEL.editMeta(m -> m.displayName(Component.text(ChatColor.RED + "Cancel")));

        DELETE = new ItemStack(Material.TNT);
        DELETE.editMeta(m -> m.displayName(Component.text(ChatColor.RED  + "Delete")));

        BACK = new ItemStack(Material.BARRIER);
        BACK.editMeta(m -> m.displayName(Component.text(ChatColor.RED  + "Back")));

        RENAME = new ItemStack(Material.ANVIL);
        RENAME.editMeta(m -> m.displayName(Component.text(ChatColor.RED + "Rename")));

        CHANGE = new ItemStack(Material.CRAFTING_TABLE);
        CHANGE.editMeta(m -> m.displayName(Component.text(ChatColor.RED + "Change")));

        ADMIN_PANEL = new ItemStack(Material.WRITABLE_BOOK);
        ADMIN_PANEL.editMeta(m -> m.displayName(Component.text(ChatColor.RED + "Tools")));
    }

    private final List<Integer> recipeIndices = List.of(10, 11, 12, 19, 20, 21, 28, 29, 30);
    private final List<Integer> ALLOWED_INDICES = List.of(10, 11, 12, 19, 20, 21, 28, 29, 30, 23);
    private final int RESULT_SLOT = 23;
    private final int ADMIN_PANEL_SLOT = 8;

    public Map<NamespacedKey, Recipe> recipes;
    private Map<ItemStack, Inventory> inventories;
    private Map<ItemStack, Inventory> usedIn;

    private Map<Inventory, String> creationQueue;

    private boolean includeVanilla;

    public RecipeGUI(Core plugin) {
        super(plugin, "Recipes", 27);
        inventories = new LinkedHashMap<>();
        usedIn = new HashMap<>();
        creationQueue = new HashMap<>();
        includeVanilla = plugin.config.getBoolean("items.recipes.include-vanilla");
        recipes = new HashMap<>();
        onEnable();
    }
//region enable
    public void onEnable() {
        registerCustomItems();
    }
    public void registerCustomItems() {
        var file = new File(plugin.customItemPath);
        for(var f : file.listFiles()) {
            var yaml = plugin.loadConfig(f.getPath(), false);
            var patterList = yaml.getStringList("pattern");
            var pattern = new String[patterList.size()];
            patterList.toArray(pattern);
            var sec = yaml.getConfigurationSection("map");
            Map<String, ItemStack> map = (Map)sec.getValues(false);

            var item = yaml.getItemStack("result");
            var nKey = new NamespacedKey(plugin, f.getName());
            var recipe = Bukkit.getRecipesFor(item);
            checkDuplicates(item, recipe);
            var recipe1 = new ShapedRecipe(nKey, item);
            recipe1.shape(pattern);
            for(var entry : map.entrySet()) {
                recipe1.setIngredient(entry.getKey().charAt(0), entry.getValue());
            }
            Bukkit.addRecipe(recipe1);
            recipes.put(nKey, recipe1);
        }
    }

    private void checkDuplicates(ItemStack item, List<Recipe> recipe) {
        if(recipe.size() > 1) {
            plugin.getLogger().info("Multiple recipes for " + item.getI18NDisplayName() + " detected");
            for(int i =0; i<recipe.size(); i++) {
                var s = (ShapedRecipe)recipe.get(i);
                plugin.getLogger().info(String.join("\n",s.getShape()));
            }
        }
    }

    //endregion
    private void setup() {
        //todo: implement has changed with a custom list to cache the resuts so it doesn't need to recalcuate every time
        inventory.clear();
        var items = recipes.values().stream().map(recipe -> recipe.getResult());
        var cachedItems = inventories.keySet();
        if (!cachedItems.equals(items)) {
            //the configuration has changed so invalidate cache
            inventories.clear();
            usedIn.clear();
        }

        items.forEach(i -> inventory.addItem(i));


    }

    public Inventory getBlankCrafting() {
        var inv = Bukkit.createInventory(null, 45);
        for (int i = 0; i < inv.getSize(); i++)
            inv.setItem(i, FILLER);
        for (var i : recipeIndices)
            inv.clear(i);
        inv.clear(23);
        inv.setItem(43, CANCEL);
        inv.setItem(44, CONFIRM);
        return inv;
    }

    @Override
    public void openInventory(HumanEntity e) {
        setup();
        super.openInventory(e);
    }

    //todo: implement multiple recipes for the same item;
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        var inv = e.getInventory();
        if (inv.equals(inventory)) {
            e.setCancelled(true);
            handleClickOnRecipe(e);
        }
        //if cached inv already contains the next screen inventories should not contain the main inventory instance
        else if (inventories.containsValue(inv)) { //maybe find a better way to do it
            e.setCancelled(true);
            //then need to handle back and forth;
            var slot = e.getRawSlot();
            if (slot == 43 || slot == 44)  //i
                handleBackAndNext(e);
            else if (slot == 23) //click on result, which will show what can be crafted with the item
                handleClickOnResult(e);
            else if (recipeIndices.contains(slot))
                handleClickOnRecipe(e);
            else if(slot ==ADMIN_PANEL_SLOT && e.getWhoClicked().hasPermission("ender.core.egui.admin"))
                e.getWhoClicked().openInventory(getAdminPanel(inv.getItem(RESULT_SLOT)));

            else
                return;
        } else if (creationQueue.containsKey(inv)) {
            handleRecipeCreation(e);
        } else if(e.getView().title().toString().contains(": Tools")) { //need a better way to detect specific inventory
            handleAdminPanel(e);
        } else if(e.getView().getType() == InventoryType.ANVIL && (e.getInventory().getHolder() == null || !(e.getInventory().getHolder() instanceof BlockInventoryHolder)))
            handleAdminRename(e);
    }

    //in order for this to work, the calling has to add the name to the persistent data
    //todo: find bugs
    //region recipe creation
    public void createRecipe(Player p, String name) {
        var inv = getBlankCrafting();
        p.openInventory(inv);
        creationQueue.put(inv, name);

    }
    private void handleRecipeCreation(final InventoryClickEvent e) {
        var inv = e.getInventory();
        //check if clicking other than allowed
        if(e.getClickedInventory().equals(inv) && !ALLOWED_INDICES.contains(e.getRawSlot())){
            e.setCancelled(true);
        }
        if(e.getCurrentItem() == null) {
            return;
        }
        //check to see which button;
        if(e.getCurrentItem().equals(CANCEL)) {
            e.getView().close();
            return;
        }
        else if(!e.getCurrentItem().equals(CONFIRM)) {
            return;
        }
        var result = e.getInventory().getItem(RESULT_SLOT);
        if(result == null){
            e.getWhoClicked().sendMessage("Result can not be null");
            return;
        }
        if(result.getType().isAir()){
            e.getWhoClicked().sendMessage("Item result can not be air");
            return;
        }
        var i = new ItemStack[9];
        int air =0;
        for(int slot = 0; slot < 9; slot++) {
            var item = inv.getItem(recipeIndices.get(slot));
            if(item == null){
                item = AIR;
                i[slot]=item;
            }
            if (item.getType().isAir()) {
                air++;
            } else {
                i[slot] = item;
            }
        }
        if(air ==9) {
            e.getWhoClicked().sendMessage("Recipe can not be empty");
            return;
        }
        var name = creationQueue.get(inv);

        creationQueue.remove(inv);
        var meta = result.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.itemName, PersistentDataType.STRING, name);
        result.setItemMeta(meta);
        //the name of the custom item is stored in the custom data
        var key = new NamespacedKey(plugin, name);
        //remove recipe because it is re registering,
        //todo: check if bug because recipe might not exist
        Bukkit.removeRecipe(key);
        var p = (Player) e.getWhoClicked();
        //todo: check to see if item already exists
        //check for if recipe if air and fill array


        //the first item is the result, we already have that
        var recipe = stackToRecipe(i, result, name);
        recipes.put(recipe.getKey(), recipe);
        Bukkit.addRecipe(recipe);
        inv.close();

    }

    private ShapedRecipe stackToRecipe(ItemStack[] items, ItemStack result, String name) {
        Map<Character, ItemStack> map = new HashMap<>();
        char letter = 'a';
        //generate map
        for (var item : items) {
            if (!map.values().contains(item) && (item != null && !item.getType().isAir())) {
                map.put(letter, item);
                letter++;
            }
        }
        //end map

        var path = "plugins/EnderCore/custom-items/" + name;
        var file = plugin.loadConfig(path.toString(), true);
        var pattern = convertToPattern(items, map);
        var nKey = new NamespacedKey(plugin, name);
        var recipe = new ShapedRecipe(nKey, result);
        recipe.shape(pattern);
        for (var entry : map.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue());
        }
        file.set("pattern", pattern);
        file.set("map", map);
        file.set("result", result);
        try {
            file.save(new File(path));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return recipe;
    }

    private String[] convertToPattern(ItemStack[] items, Map<Character, ItemStack> map) {
        var strings = new String[3];
        for (int s = 0; s < 3; s++)
            strings[s] = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                var item = items[i * 3 + j];
                if (item.getType() == Material.AIR)
                    strings[i] += " ";
                else {
                    char letter = 'a';
                    for (var k : map.keySet()) {
                        if (map.get(k).isSimilar(item)) {
                            strings[i] += k.toString();
                            break;
                        }
                    }
                }
            }
        }
        if (strings[0].equals("   ")) {
            return Arrays.copyOfRange(strings, 1, 3);
        } else if (strings[2].equals("   ")) {
            return Arrays.copyOfRange(strings, 0, 2);
        } //need to add case for middle

        return strings;
    }
    //endregion

    //region recipes view
    private void handleBackAndNext(final InventoryClickEvent e) {
        //i think that this just does the same thing as recipe for now
        if(e.getRawSlot() == 43)
            e.getWhoClicked().openInventory(inventory);
    }


    private void handleClickOnResult(final InventoryClickEvent e) {
        //this uses the used in cache
        //this will miss some
        //need to change to a list
        if (!includeVanilla) //ignore for now
            return;

        var item = e.getCurrentItem();
        var inv = usedIn.get(item);
        if (inv != null) {
            e.getWhoClicked().openInventory(inv);
            return;
        }
        var entries = inventories.entrySet();
        inv = Bukkit.createInventory(null, 27); //27 for now
        for (var i : entries) {
            var inven = i.getValue();
            if (inven.contains(item) && !inven.getItem(23).equals(item))
                //add until the last 2
                inv.addItem(i.getKey());
        }
        usedIn.put(item, inv);
        e.getWhoClicked().openInventory(inv);


    }

    private void handleClickOnRecipe(final InventoryClickEvent e) {
        //e.getWhoClicked().sendMessage("STOP");


        var item = e.getCurrentItem();
        var inv = inventories.get(item);
        if (inv != null) {
            //this can be before includeVanilla because it contains already cached which means it already checked.
            e.getWhoClicked().openInventory(inv);
            return;
        }
        //todo: implement vanilla
        if (!includeVanilla) {
            if (item == null || !item.getItemMeta().getPersistentDataContainer().has(plugin.itemName, PersistentDataType.STRING))
                return;
        }


        var recipe = Bukkit.getRecipesFor(item);
        //error checking
        if (recipes.size() == 0)
            plugin.getLogger().info("No recipes for " + item.getI18NDisplayName() + " detected");
        else checkDuplicates(item, recipe);
        //end error checking
        var r = recipe.get(0);
        var p = e.getWhoClicked();

        if (r instanceof ShapedRecipe) {
            ItemStack next = null;
            var index = e.getRawSlot();
            //this is the last slot
            if (index == inventory.getSize() - 1)
                next = FILLER;
            else
                next = inventory.getItem(index + 1);
            ItemStack back = null;
            if (index == 0)
                back = new ItemStack(Material.CRAFTING_TABLE);
            else if (index == inventory.getSize() - 1) //only for if there is only one item
                back = FILLER;
            else
                back = inventory.getItem(index - 1);
            inv = recipeToInventory((ShapedRecipe) r, FILLER, CRAFTING_TABLE);
            if(p.hasPermission("ender.core.egui.admin")) {
                inv.setItem(ADMIN_PANEL_SLOT,ADMIN_PANEL);
            }
            p.openInventory(inv);
        }
    }

    public Inventory recipeToInventory(ShapedRecipe recipe, ItemStack next, ItemStack back) {
        var key = recipe.getKey();
        var result = recipe.getResult();
        var existing = inventories.get(result);
        if (existing != null) {
            //next back are upgrade all of the time
            existing.setItem(43, back);
            existing.setItem(44, next);
            return existing;
        }
        var stack = recipeToStack(recipe);

        var inv = Bukkit.createInventory(null, 45, Component.text(StringUtils.capitalize(key.getKey())));
        for (int i = 0; i < inv.getSize(); i++)
            inv.setItem(i, FILLER);

        //todo: maybe make this more elegant?
        var contents = inv.getContents();
        contents[23] = stack[0];

        contents[10] = stack[1];
        contents[11] = stack[2];
        contents[12] = stack[3];

        contents[19] = stack[4];
        contents[20] = stack[5];
        contents[21] = stack[6];

        contents[28] = stack[7];
        contents[29] = stack[8];
        contents[30] = stack[9];

        contents[43] = back;
        contents[44] = next;
        //fill the rest with the filler
        inv.setContents(contents);
        inventories.put(result, inv);
        return inv;
    }

    public static ItemStack[] recipeToStack(ShapedRecipe recipe) {
        var stack = new ItemStack[10];
        stack[0] = recipe.getResult();
        var shape = recipe.getShape();
        var map = recipe.getIngredientMap();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length(); j++) {
                var item = map.get(shape[i].charAt(j));
                if (item == null || item.getType().isAir())
                    item = AIR_REPLACE;
                stack[i * 3 + j + 1] = item;
            }
        }

        return stack;
    }

    //endregion

    //region recipe admin
    private void handleAdminPanel(final InventoryClickEvent e) {
        e.setCancelled(true);
        var slot = e.getRawSlot();
        var item = e.getInventory().getItem(0);
        switch(slot) {
            case 0: //give
                if(e.getClick() == ClickType.MIDDLE) {
                    item.setAmount(64);
                    e.getWhoClicked().getInventory().addItem(item);
                }
                else
                e.getWhoClicked().getInventory().addItem(item);
                break;
            case 2: //rename
                var inv = e.getWhoClicked().openAnvil(null, true);
                inv.setItem(0, item);
                break;
            case 3: //change
                break;
            case 4: //delete
                try {
                    var fname = item.getItemMeta().getPersistentDataContainer().get(plugin.itemName, PersistentDataType.STRING);
                    if(Files.deleteIfExists(Path.of(plugin.customItemPath +"/" + fname))) {
                        e.getWhoClicked().sendMessage(fname + " deleted successfully");
                        recipes.remove(NamespacedKey.fromString(fname, plugin));
                        inventory.remove(item);
                    }
                    else {
                        e.getWhoClicked().sendMessage(fname + " not found");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case 8: //back main inventory or the other one
                e.getWhoClicked().openInventory(inventory);
                break;
        }
    }
    private void handleAdminRename(InventoryClickEvent e) {
        e.setCancelled(true);
        var inv = (AnvilInventory)e.getInventory();
        int slot = e.getRawSlot();
        if(slot != 2) {
            return;
        }
        if(slot == 3) {
            var key = (ShapedRecipe)recipes.values().stream().filter(r -> r.getResult().equals(inv.getFirstItem())).findFirst().get();


        }
        var item = inv.getFirstItem().clone();
        item.editMeta(m -> m.displayName(Component.text(inv.getRenameText())));
        inv.setResult(item);
    }

    private Inventory getAdminPanel(ItemStack item) {
        var inv = Bukkit.createInventory(null, 9, Component.text(item.getI18NDisplayName() + ": Tools" + "§a§5§5§7§oASS"));
        inv.setItem(0, item);
        inv.setItem(2, RENAME); //slot 3
        inv.setItem(3, CHANGE); //change map to <
        inv.setItem(4, DELETE);
        inv.setItem(8, BACK);
        return inv;
    }
    //endregion
    @Override
    @EventHandler
    public void onReset(PluginReloadEvent e) {
        includeVanilla = e.getPlugin().config.getBoolean("items.recipes.include-vanilla");
        reload();
    }

    public void reload() {
        inventories.clear();
        usedIn.clear();
    }
}
