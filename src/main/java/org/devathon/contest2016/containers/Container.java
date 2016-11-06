package org.devathon.contest2016.containers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.devathon.contest2016.HoldingMachine;
import org.devathon.contest2016.items.MovingItem;

public abstract class Container implements HoldingMachine{

    private Block block;

    public Container(Block block){
        this.block = block;
    }

    public Block getBlock(){
        return block;
    }

    @Override
    public ItemStack[] pullAndRemove(){
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
                return new ItemStack[]{newis};
            }
        }
        return null;
    }

    @Override
    public boolean acceptItem(MovingItem movingItem){
        ItemStack item = movingItem.getItem().getItemStack();
        ItemStack[] iss = getContents();
        int firstAvailable = -1;
        for (int i = 0; i < iss.length; i++){
            ItemStack is = iss[i];
            if ((is == null || is.getType() == Material.AIR) && firstAvailable == -1)
                firstAvailable = i;
            else if (is != null && is.getType() == item.getType() && is.getAmount() < is.getMaxStackSize()){
                iss[i].setAmount(is.getAmount() + 1);
                setContents(iss);
                return true;
            }
        }
        if (firstAvailable != -1){
            iss[firstAvailable] = item;
            setContents(iss);
            return true;
        }
        return false;
    }

    @Override
    public Location getLocation(){
        return block.getLocation();
    }

    public abstract ItemStack[] getContents();

    public abstract void setContents(ItemStack[] items);
}
