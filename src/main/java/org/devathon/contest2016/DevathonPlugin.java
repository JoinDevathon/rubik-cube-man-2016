package org.devathon.contest2016;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.devathon.contest2016.config.ConfigSaver;
import org.devathon.contest2016.items.ItemHandler;
import org.devathon.contest2016.machines.MachineHandler;
import org.devathon.contest2016.rails.RailHandler;

public class DevathonPlugin extends JavaPlugin {

    @Override
    public void onEnable(){
        clearWorlds();
        new MachineHandler(this);
        new RailHandler(this);
        new ItemHandler(this);
        createRecipes();
        new ConfigSaver(this);
    }

    @Override
    public void onDisable() {
        saveMachineConfig();
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

    private void clearWorlds(){
        //Because armor stands get saved by bukkit... so we remove them
        for (World world : Bukkit.getWorlds()){
            world.getEntities().stream().filter(e -> e instanceof ArmorStand).forEach(e -> {
                ArmorStand stand = (ArmorStand) e;
                if (!stand.isVisible()){
                    if (stand.getPassenger() != null)
                        stand.getPassenger().remove();
                    stand.remove();
                }
            });
        }
    }

    private void saveMachineConfig(){
        ConfigSaver.getInstance().save();
    }
}

