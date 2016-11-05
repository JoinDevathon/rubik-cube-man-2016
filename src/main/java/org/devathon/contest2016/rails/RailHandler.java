package org.devathon.contest2016.rails;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.devathon.contest2016.MachinePart;
import org.devathon.contest2016.containers.Container;
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
                if (part instanceof Container){
                    Container container = (Container) part;
                    ItemStack stack = container.getAndRemoveFirst();
                    if (stack != null)
                        new MovingItem(stack, rail, connector);
                    return;
                }
            }
        }
    }
}
