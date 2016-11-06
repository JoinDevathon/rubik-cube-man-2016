package org.devathon.contest2016.containers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ChestContainer extends Container{

    private Chest chest;

    public ChestContainer(Block block){
        super(block);
        this.chest = (Chest) block.getState();
    }

    @Override
    public ItemStack[] getContents(){
        BlockFace doubleChest = doubleChest();
        if (doubleChest == null)
            return chest.getBlockInventory().getContents();
        ItemStack[] iss = new ItemStack[54];
        Chest otherChest = (Chest) chest.getBlock().getRelative(doubleChest).getState();
        System.arraycopy(chest.getBlockInventory().getContents(), 0, iss, (doubleChest == BlockFace.NORTH || doubleChest == BlockFace.WEST) ? 27 : 0, 27);
        System.arraycopy(otherChest.getBlockInventory().getContents(), 0, iss, (doubleChest == BlockFace.NORTH || doubleChest == BlockFace.WEST) ? 0 : 27, 27);
        return iss;
    }

    @Override
    public void setContents(ItemStack[] items){
        if (items.length == 54){
            BlockFace doubleChest = doubleChest();
            if (doubleChest != null){
                ItemStack[] first = Arrays.copyOfRange(items, 0, 27);
                ItemStack[] second = Arrays.copyOfRange(items, 27, 54);
                if (doubleChest == BlockFace.NORTH || doubleChest == BlockFace.WEST){
                    ItemStack[] temp = first;
                    first = second;
                    second = temp;
                }
                chest.getBlockInventory().setContents(first);
                chest.update();
                Chest otherChest = (Chest) chest.getBlock().getRelative(doubleChest).getState();
                otherChest.getBlockInventory().setContents(second);
                otherChest.update();
                return;
            }
            items = Arrays.copyOfRange(items, 0, 27);
        }
        chest.getBlockInventory().setContents(items);
        chest.update();
    }

    private BlockFace doubleChest(){
        for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}){
            if (chest.getBlock().getRelative(face).getType() == Material.CHEST)
                return face;
        }
        return null;
    }
}
