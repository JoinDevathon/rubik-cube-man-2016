package org.devathon.contest2016;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.devathon.contest2016.items.ItemHandler;
import org.devathon.contest2016.rails.RailHandler;

public class DevathonPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new MachineHandler(this);
        new RailHandler(this);
        new ItemHandler(this);
        createRecipes();
    }

    @Override
    public void onDisable() {
        // put your disable code here
    }

    private void createRecipes(){
        ItemStack craftingMachine = new ItemStack(Material.WORKBENCH);
        ItemMeta craftingMachineMeta = craftingMachine.getItemMeta();
        craftingMachineMeta.setDisplayName(ChatColor.GOLD + "Crafting Machine");
        craftingMachine.setItemMeta(craftingMachineMeta);
        ShapedRecipe recipe = new ShapedRecipe(craftingMachine);
        recipe.shape("wdw", "wcw", "www");
        recipe.setIngredient('w', Material.WOOD);
        recipe.setIngredient('c', Material.WORKBENCH);
        recipe.setIngredient('d', Material.DIAMOND_PICKAXE);
        Bukkit.addRecipe(recipe);
    }
}

