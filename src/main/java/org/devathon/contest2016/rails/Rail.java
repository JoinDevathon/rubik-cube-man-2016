package org.devathon.contest2016.rails;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Rails;
import org.devathon.contest2016.machines.MachinePart;

import java.util.HashSet;
import java.util.Set;

public abstract class Rail implements MachinePart{

    private Block block;
    private Rails rails;

    public Rail(Block block){
        this.block = block;
        this.rails = (Rails) block.getState().getData();
    }

    public RailConnector[] getConnections(){
        BlockFace direction = rails.getDirection();
        if (rails.isCurve()){
            switch (direction){
                case NORTH_EAST:
                    return new RailConnector[]{RailConnector.SOUTH, RailConnector.WEST};
                case SOUTH_EAST:
                    return new RailConnector[]{RailConnector.NORTH, RailConnector.WEST};
                case NORTH_WEST:
                    return new RailConnector[]{RailConnector.SOUTH, RailConnector.EAST};
                case SOUTH_WEST:
                    return new RailConnector[]{RailConnector.NORTH, RailConnector.EAST};
            }
        }
        if (rails.isOnSlope()){
            switch (direction){
                case NORTH:
                    return new RailConnector[]{RailConnector.NORTH_UPPER, RailConnector.SOUTH};
                case SOUTH:
                    return new RailConnector[]{RailConnector.SOUTH_UPPER, RailConnector.NORTH};
                case EAST:
                    return new RailConnector[]{RailConnector.EAST_UPPER, RailConnector.WEST};
                case WEST:
                    return new RailConnector[]{RailConnector.WEST_UPPER, RailConnector.EAST};
            }
        }
        switch (direction){
            case EAST:
                return new RailConnector[]{RailConnector.EAST, RailConnector.WEST};
            case SOUTH:
                return new RailConnector[]{RailConnector.SOUTH, RailConnector.NORTH};
        }
        return new RailConnector[]{RailConnector.SOUTH, RailConnector.NORTH};
    }

    public RailConnector getConnector(RailConnector connector){
        RailConnector[] connections = getConnections();
        for (RailConnector c : connections){
            if (c.getDirection() != connector.getDirection()){
                return c;
            }
        }
        return RailConnector.getByDirection(connector.getDirection().getOppositeFace(), false);
    }

    public MachinePart[] getConnectedParts(){
        RailConnector[] connectors = getConnections();
        Set<MachinePart> parts = new HashSet<>(connectors.length);
        for (RailConnector connector : connectors){
            MachinePart part = connector.getRelative(block);
            if (part != null)
                parts.add(part);
        }
        return parts.toArray(new MachinePart[parts.size()]);
    }

    public boolean isSlope(){
        return rails.isOnSlope();
    }

    public Block getBlock(){
        return block;
    }

    @Override
    public Location getLocation(){
        return block.getLocation();
    }

    @Override
    public void broken(){
        //TODO Drop items on the rail.
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rail rail = (Rail) o;
        return block != null ? block.equals(rail.block) : rail.block == null;
    }

    @Override
    public int hashCode(){
        return block != null ? block.hashCode() : 0;
    }

    public abstract double calculateVelocity(double velocity);

    public Rails getRails(){
        return rails;
    }
}
