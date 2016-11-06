package org.devathon.contest2016.machines.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.EditableMachine;
import org.devathon.contest2016.HoldingMachine;
import org.devathon.contest2016.items.MovingItem;

import java.util.*;

public class CraftingMachine implements HoldingMachine, EditableMachine{

    private static final int RESULT_SLOT = 16;
    private Block block;
    private ItemStack[] iss;
    private Inventory inv;

    public CraftingMachine(Block block){
        this.block = block;
        if (block.hasMetadata("items"))
            this.iss = (ItemStack[]) block.getMetadata("items").get(0).value();
        else
            block.setMetadata("items", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), this.iss = new ItemStack[9]));
        if (block.hasMetadata("inv"))
            this.inv = (Inventory) block.getMetadata("inv").get(0).value();
        else
            block.setMetadata("inv", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), this.inv = createInventory()));
    }

    private Inventory createInventory(){
        Inventory inv = Bukkit.createInventory(null, 27, "Crafting Machine");
        ItemStack placeHolder = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta meta = placeHolder.getItemMeta();
        meta.setDisplayName(" ");
        placeHolder.setItemMeta(meta);
        for (int i = 0; i < 27; i++){
            if (isPlaceholder(i))
                inv.setItem(i, placeHolder);
        }
        return inv;
    }

    private boolean isPlaceholder(int slot){
        return !isEditable(slot) && slot != RESULT_SLOT;
    }

    private boolean isEditable(int slot){
        return slot >= 3 && slot <= 5 || slot >= 12 && slot <= 14 || slot >= 21 && slot <= 23 || slot >= 27;
    }

    @EventHandler
    public void open(Player player){
        player.openInventory(inv);
    }

    @Override
    public void drag(InventoryDragEvent event){
        Iterator<Integer> itr = event.getNewItems().keySet().iterator();
        while (itr.hasNext()){
            Integer slot = itr.next();
            if (!isEditable(slot))
                itr.remove();
        }
        updateResultDelayed();
    }

    @Override
    public void click(InventoryClickEvent event){
        if (!isEditable(event.getRawSlot())){
            event.setCancelled(true);
            return;
        }
        updateResultDelayed();
    }

    private void updateResultDelayed(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(DevathonPlugin.getPlugin(DevathonPlugin.class), () -> {
            for (int i = 0; i < 9; i++)
                iss[i] = inv.getItem(arraySlotToSlot(i));
            updateResult();
        });
    }

    @Override
    public boolean acceptItem(MovingItem item){
        Map<Integer, Integer> map = new HashMap<>(3);
        for (int i = 0; i < iss.length; i++){
            ItemStack is = iss[i];
            if (is != null && is.getType() == item.getItem().getItemStack().getType() && is.getAmount() < is.getMaxStackSize())
                map.put(i, is.getAmount());
        }
        if (map.isEmpty())
            return false;
        int smallest = -1;
        int smallestamount = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()){
            if (entry.getValue() < smallestamount){
                smallestamount = entry.getValue();
                smallest = entry.getKey();
            }
        }
        if (iss[smallest] != null && iss[smallest].getType() == item.getItem().getItemStack().getType())
            iss[smallest].setAmount(iss[smallest].getAmount() + 1);
        else
            iss[smallest] = item.getItem().getItemStack();
        setContents(iss);
        return true;
    }

    @Override
    public ItemStack[] pullAndRemove(){
        boolean foundItem = false;
        for (ItemStack item : iss){
            if (item != null){
                if (item.getAmount() >= 2)
                    foundItem = true;
                else
                    return null;
            }
        }
        if (foundItem){
            for (ItemStack item : iss){
                if (item != null)
                    item.setAmount(item.getAmount() - 1);
            }
            setContents(iss);
            return getResult();
        }
        return null;
    }

    private ItemStack[] getResult(){
        ItemStack is = getResultFromRecipe();
        if (is == null){
            List<ItemStack> iss = new ArrayList<>(4);
            for (ItemStack item : this.iss){
                if (item != null)
                    iss.add(item);
            }
            return iss.toArray(new ItemStack[iss.size()]);
        }
        return new ItemStack[]{is};
    }

    private ItemStack getResultFromRecipe(){
        Iterator<Recipe> itr = Bukkit.recipeIterator();
        while (itr.hasNext()){
            Recipe r = itr.next();
            if (r instanceof ShapedRecipe){
                ItemStack is = testShaped((ShapedRecipe) r);
                if (is != null)
                    return is;
            } else if (r instanceof ShapelessRecipe){
                ItemStack is =  testShapeless((ShapelessRecipe) r);
                if (is != null)
                    return is;
            }
        }
        return null;
    }

    private ItemStack testShaped(ShapedRecipe sr){
        String[] shape = sr.getShape();
        for (int column = 0; column < (4 - shape[0].length()); column++){
            for (int row = 0; row < (4 - shape.length); row++){
                ItemStack is = testShaped(sr, column, row);
                if (is != null)
                    return is;
            }
        }
        return null;
    }

    private ItemStack testShaped(ShapedRecipe sr, int column, int row){
        Map<Character, ItemStack> map = sr.getIngredientMap();
        String[] shape = sr.getShape();
        int shapeHeight = shape.length;
        int shapeWidth = shape[0].length();
        for (int i = 0; i < shape.length; i++){
            String aShape = shape[i];
            for (int j = 0; j < aShape.length(); j++){
                char c = aShape.charAt(j);
                int s = ((i + row) * 3) + (column + j);
                if (map.get(c) != null){
                    if (!match(map.get(c), iss[s]))
                        return null;
                } else if (iss[s] != null)
                    return null;
            }
        }
        for (int i = 0; i < row; i++){
            for (int j = 0; j < column; j++){
                int s = (i * 3) + j;
                if (iss[s] != null)
                    return null;
            }
        }
        for (int i = shapeHeight + row; i < 3; i++){
            for (int j = shapeWidth + column; j < 3; j++){
                int s = (i * 3) + j;
                if (iss[s] != null)
                    return null;
            }
        }
        return sr.getResult();
    }

    private boolean match(ItemStack item, ItemStack checkWith){
        if (item == null && checkWith == null)
            return true;
        if (item == null || checkWith == null)
            return false;
        if (item.getType() == checkWith.getType()){
            if (item.getDurability() == checkWith.getDurability() || item.getDurability() == 32767){
                if (Bukkit.getItemFactory().equals(item.getItemMeta(), checkWith.getItemMeta()))
                    return true;
            }
        }
        return false;
    }

    private ItemStack testShapeless(ShapelessRecipe sr){
        List<ItemStack> items = sr.getIngredientList();
        int itemsNeeded = items.size();
        int itemCount = 0;
        for (ItemStack is : iss){
            if (is != null){
                itemCount++;
                for (ItemStack item : items){
                    if (match(item, is)){
                        items.remove(item);
                        break;
                    }
                }
            }
        }
        if (itemCount == itemsNeeded && items.isEmpty())
            return sr.getResult();
        return null;
    }

    private void setContents(ItemStack[] iss){
        this.iss = iss;
        for (int i = 0; i < iss.length; i++)
            inv.setItem(arraySlotToSlot(i), iss[i]);
        updateResult();
    }

    public void updateResult(){
        inv.setItem(RESULT_SLOT, getResultFromRecipe());
    }

    private int invSlotToSlot(int inventorySlot){
        int rows = inventorySlot / 9;
        return ((inventorySlot % 9) - 3) + (rows * 3);
    }

    private int arraySlotToSlot(int craftingSlot){
        return 3 + (craftingSlot % 3) + ((craftingSlot / 3) * 9);
    }

    @Override
    public Location getLocation(){
        return block.getLocation();
    }
}
