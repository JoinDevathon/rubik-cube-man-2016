package org.devathon.contest2016.containers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.devathon.contest2016.MachinePart;

public abstract class Container implements MachinePart{

    private Block block;

    public Container(Block block){
        this.block = block;
    }

    public Block getBlock(){
        return block;
    }

    public ItemStack getAndRemoveFirst(){
        ItemStack[] contents = getContents();
        for (int i = 0; i < contents.length; i++){
            ItemStack is = contents[i];
            if (is != null && is.getType() != Material.AIR && is.getAmount() != 0){
                if (is.getAmount() > 1)
                    is.setAmount(is.getAmount() - 1);
                else
                    contents[i] = null;
                setContents(contents);
                ItemStack newis = is.clone();
                newis.setAmount(1);
                return newis;
            }
        }
        return null;
    }

    public abstract ItemStack[] getContents();

    public abstract void setContents(ItemStack[] items);
}
