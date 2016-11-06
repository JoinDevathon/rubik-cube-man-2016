package org.devathon.contest2016;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.devathon.contest2016.machines.crafting.CraftingMachine;

public class MachineHandler implements Listener{

    private static final String OPEN_MENU_METADATA = "openmachine";

    public MachineHandler(Plugin plugin){
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND){
            MachinePart part = MachinePart.partFromBlock(event.getClickedBlock());
            if (part instanceof EditableMachine){
                EditableMachine machine = (EditableMachine) part;
                machine.open(event.getPlayer());
                event.getPlayer().setMetadata(OPEN_MENU_METADATA, new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), machine));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if (event.getPlayer().hasMetadata(OPEN_MENU_METADATA))
            event.getPlayer().removeMetadata(OPEN_MENU_METADATA, DevathonPlugin.getPlugin(DevathonPlugin.class));
    }

    @EventHandler
    public void onEdit(InventoryDragEvent event){
        if (event.getWhoClicked() instanceof Player){
            Player player = (Player) event.getWhoClicked();
            if (player.hasMetadata(OPEN_MENU_METADATA)){
                EditableMachine machine = (EditableMachine) player.getMetadata(OPEN_MENU_METADATA).get(0).value();
                machine.drag(event);
            }
        }
    }

    @EventHandler
    public void onEdit(InventoryClickEvent event){
        if (event.getWhoClicked() instanceof Player){
            Player player = (Player) event.getWhoClicked();
            if (player.hasMetadata(OPEN_MENU_METADATA)){
                EditableMachine machine = (EditableMachine) player.getMetadata(OPEN_MENU_METADATA).get(0).value();
                machine.click(event);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        String name = event.getItemInHand().getItemMeta().getDisplayName();
        if (event.getBlockPlaced().getType() == Material.WORKBENCH && name != null && name.contains("Crafting Machine")){
            event.getBlock().setMetadata("craftingmachine", new FixedMetadataValue(DevathonPlugin.getPlugin(DevathonPlugin.class), "MasterHub"));
            new CraftingMachine(event.getBlock());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        MachinePart part = MachinePart.partFromBlock(event.getBlock());
        if (part != null)
            part.broken();
    }
}
