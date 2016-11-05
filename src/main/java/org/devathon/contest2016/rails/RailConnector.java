package org.devathon.contest2016.rails;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public enum RailConnector{

    NORTH(BlockFace.NORTH, false),
    SOUTH(BlockFace.SOUTH, false),
    EAST(BlockFace.EAST, false),
    WEST(BlockFace.WEST, false),
    NORTH_UPPER(BlockFace.NORTH, true),
    SOUTH_UPPER(BlockFace.SOUTH, true),
    EAST_UPPER(BlockFace.EAST, true),
    WEST_UPPER(BlockFace.WEST, true);

    RailConnector(BlockFace direction, boolean upper){
        this.direction = direction;
        this.upper = upper;
    }

    private BlockFace direction;
    private boolean upper;

    public BlockFace getDirection(){
        return direction;
    }

    public boolean isUpper(){
        return upper;
    }

    public Block getBlockRelative(Block block){
        block = block.getRelative(direction);
        if (upper)
            block = block.getRelative(BlockFace.UP);
        return block;
    }

    public static RailConnector getByDirection(BlockFace face, boolean upper){
        for (RailConnector connector : values()){
            if (connector.getDirection() == face && connector.isUpper())
                return connector;
        }
        return RailConnector.NORTH;
    }
}
