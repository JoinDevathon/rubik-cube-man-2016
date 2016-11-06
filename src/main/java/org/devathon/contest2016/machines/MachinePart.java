package org.devathon.contest2016.machines;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.devathon.contest2016.containers.ChestContainer;
import org.devathon.contest2016.machines.crafting.CraftingMachine;
import org.devathon.contest2016.rails.NormalRail;
import org.devathon.contest2016.rails.PoweredRail;
import org.devathon.contest2016.rails.RailConnector;
import org.devathon.contest2016.rails.SuckyRail;

public interface MachinePart{

    static MachinePart partFromBlock(Block block){
        switch (block.getType()){
            case RAILS:
                return new NormalRail(block);
            case ACTIVATOR_RAIL:
                return new SuckyRail(block);
            case POWERED_RAIL:
                return new PoweredRail(block);
            case CHEST:
                return new ChestContainer(block);
            case WORKBENCH:
                if (block.hasMetadata("craftingmachine"))
                    return new CraftingMachine(block);
        }
        return null;
    }

    default MachinePart getRelative(RailConnector connector){
        Location loc = getLocation().clone();
        return connector.getRelative(loc.getBlock());
    }

    Location getLocation();

    void broken();
}
