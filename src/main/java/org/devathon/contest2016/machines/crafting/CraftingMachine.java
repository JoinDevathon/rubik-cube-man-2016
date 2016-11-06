package org.devathon.contest2016.machines.crafting;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.config.ConfigSaver;
import org.devathon.contest2016.config.SaveableObject;
import org.devathon.contest2016.config.Utils;
import org.devathon.contest2016.machines.EditableMachine;
import org.devathon.contest2016.machines.HoldingMachine;
import org.devathon.contest2016.items.MovingItem;

import java.util.*;

public class CraftingMachine implements HoldingMachine, EditableMachine, SaveableObject{

    private static final int RESULT_SLOT = 16;
    private final Block block;
    private ItemStack[] iss;
    private final Inventory inv;

    public CraftingMachine(Block block){
        this.block = block;
        if (!block.hasMetadata("items")){
            block.setMetadata("items", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), this.iss = new ItemStack[9]));
            block.setMetadata("inv", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), this.inv = createInventory()));
            block.setMetadata("craftingmachine", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), "MasterHub"));
            createArmourStand();
        } else {
            this.iss = (ItemStack[]) block.getMetadata("items").get(0).value();
            this.inv = (Inventory) block.getMetadata("inv").get(0).value();
        }
    }

    private CraftingMachine(Location loc, ItemStack[] iss){
        this.block = loc.getBlock();
        block.setMetadata("items", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), this.iss = iss));
        block.setMetadata("inv", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), this.inv = createInventory()));
        block.setMetadata("craftingmachine", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), "MasterHub"));
        createArmourStand();
        setContents(iss);
    }

    private void createArmourStand(){
        ArmorStand stand = (ArmorStand) block.getWorld().spawnEntity(block.getLocation().add(0.5, 1.0, 0.5), EntityType.ARMOR_STAND);
        stand.setMarker(true);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(ChatColor.GOLD + "Crafting machine");
        stand.setRemoveWhenFarAway(false);
        block.setMetadata("floatingtext", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), stand));
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
            getLocation().getWorld().playEffect(getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
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
        boolean[] checked = new boolean[9];
        for (int i = 0; i < checked.length; i++)
            checked[i] = false;
        for (int i = 0; i < shape.length; i++){
            String aShape = shape[i];
            for (int j = 0; j < aShape.length(); j++){
                char c = aShape.charAt(j);
                int s = ((i + row) * 3) + (column + j);
                checked[s] = true;
                if (map.get(c) != null){
                    if (!match(map.get(c), iss[s]))
                        return null;
                } else if (iss[s] != null)
                    return null;
            }
        }
        for (int i = 0; i < checked.length; i++){
            if (!checked[i]){
                if (iss[i] != null)
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

    @Override
    public void broken(){
        Location loc = block.getLocation();
        for (ItemStack is : iss){
            if (is != null)
                loc.getWorld().dropItemNaturally(loc, is);
        }
        ArmorStand stand = (ArmorStand) block.getMetadata("floatingtext").get(0).value();
        stand.remove();
        block.removeMetadata("craftingmachine", DevathonPlugin.getPlugin(DevathonPlugin.class));
        block.removeMetadata("items", DevathonPlugin.getPlugin(DevathonPlugin.class));
        block.removeMetadata("inv", DevathonPlugin.getPlugin(DevathonPlugin.class));
        block.removeMetadata("floatingtext", DevathonPlugin.getPlugin(DevathonPlugin.class));
        ConfigSaver.getInstance().removeObject(this);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CraftingMachine that = (CraftingMachine) o;
        return block != null ? block.equals(that.block) : that.block == null;
    }

    @Override
    public int hashCode(){
        return block != null ? block.hashCode() : 0;
    }

    @Override
    public void save(ConfigurationSection section){
        for (int i = 0; i < 9; i++)
            section.set("items." + i, iss[i]);
        Utils.saveLocation(section, getLocation(), "loc");
    }

    public static CraftingMachine load(ConfigurationSection section){
        Location loc = Utils.loadLocation(section, "loc");
        if (loc == null)
            return null;
        ItemStack[] iss = new ItemStack[9];
        for (int i = 0; i < 9; i++)
            iss[i] = section.getItemStack("items." + i);
        return new CraftingMachine(loc, iss);
    }
}
