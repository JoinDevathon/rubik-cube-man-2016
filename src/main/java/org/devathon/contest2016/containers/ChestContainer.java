package org.devathon.contest2016.containers;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class ChestContainer extends Container{

    private Chest chest;

    public ChestContainer(Block block){
        super(block);
        this.chest = (Chest) block.getState();
    }

    @Override
    public ItemStack[] getContents(){
        return chest.getBlockInventory().getContents();
    }

    @Override
    public void setContents(ItemStack[] items){
        chest.getBlockInventory().setContents(items);
        chest.update();
    }
}
