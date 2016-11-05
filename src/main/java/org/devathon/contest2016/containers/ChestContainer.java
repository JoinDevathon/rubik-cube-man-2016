package org.devathon.contest2016.containers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.devathon.contest2016.items.MovingItem;

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

    @Override
    public boolean acceptItem(MovingItem item){

        //TODO Add item to chest. If full, return false
        return true;
    }

    @Override
    public Location getLocation(){
        return chest.getLocation();
    }
}
