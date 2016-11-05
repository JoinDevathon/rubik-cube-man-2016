package org.devathon.contest2016;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.devathon.contest2016.containers.ChestContainer;
import org.devathon.contest2016.rails.NormalRail;
import org.devathon.contest2016.rails.RailConnector;
import org.devathon.contest2016.rails.SuckyRail;

public interface MachinePart{

    static MachinePart partFromBlock(Block block){
        switch (block.getType()){
            case RAILS:
                return new NormalRail(block);
            case ACTIVATOR_RAIL:
                return new SuckyRail(block);
            case CHEST:
                return new ChestContainer(block);
        }
        return null;
    }

    default MachinePart getRelative(RailConnector connector){
        Location loc = getLocation().clone();
        Block block = connector.getBlockRelative(loc.getBlock());
        return partFromBlock(block);
    }

    Location getLocation();
}
