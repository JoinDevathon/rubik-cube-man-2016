package org.devathon.contest2016.rails;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.devathon.contest2016.config.ConfigSaver;
import org.devathon.contest2016.machines.HoldingMachine;
import org.devathon.contest2016.machines.MachinePart;
import org.devathon.contest2016.items.MovingItem;

public class RailHandler implements Listener{

    private RailHandler instance;

    public RailHandler(Plugin plugin){
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void blockRedstone(BlockRedstoneEvent event){
        if (event.getNewCurrent() > 0 && event.getBlock().getType() == Material.ACTIVATOR_RAIL){
            SuckyRail rail = new SuckyRail(event.getBlock());
            RailConnector[] parts = rail.getConnections();
            for (RailConnector connector : parts){
                MachinePart part = rail.getRelative(connector);
                if (part instanceof HoldingMachine){
                    HoldingMachine machine = (HoldingMachine) part;
                    ItemStack[] stack = machine.pullAndRemove();
                    if (stack != null){
                        for (ItemStack item : stack){
                            MovingItem i = new MovingItem(item, rail, connector);
                            ConfigSaver.getInstance().addObject(i);
                        }
                    }
                    return;
                }
            }
        }
    }
}
