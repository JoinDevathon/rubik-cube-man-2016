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

public class RailHandler implements Listener{

    private RailHandler instance;

    public RailHandler(Plugin plugin){
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void blockRedstone(BlockRedstoneEvent event){
        if (event.getBlock().getType() == Material.ACTIVATOR_RAIL){
            SuckyRail rail = new SuckyRail(event.getBlock());
            MachinePart[] parts = rail.getConnectedParts();
            for (MachinePart part : parts){
                if (part instanceof Container){
                    Container container = (Container) part;
                    ItemStack stack = container.getAndRemoveFirst();

                    return;
                }
            }
        }
    }
}
