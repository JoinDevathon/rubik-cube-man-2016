package org.devathon.contest2016.rails;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.devathon.contest2016.machines.MachinePart;
import org.devathon.contest2016.util.RelativeLoc;

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
        MachinePart part = getRelative(block);
        return part == null ? null : part.getLocation().getBlock();
    }

    public MachinePart getRelative(Block block){
        block = block.getRelative(direction);
        if (upper)
            block = block.getRelative(BlockFace.UP);
        MachinePart part = MachinePart.partFromBlock(block);
        if (part == null && !upper)
            part = MachinePart.partFromBlock(block.getRelative(BlockFace.DOWN));
        return part;
    }

    public static RailConnector getByDirection(BlockFace face, boolean upper){
        for (RailConnector connector : values()){
            if (connector.getDirection() == face && connector.isUpper())
                return connector;
        }
        return RailConnector.NORTH;
    }

    public static RelativeLoc getPosInBlock(RailConnector from, RailConnector to, double amount){
        RelativeLoc loc;
        if (amount <= 0.5){
            double modX = 0.5 + ((((double) from.getDirection().getModX()) * (0.5 - amount)));
            double modZ = 0.5 + ((((double) from.getDirection().getModZ()) * (0.5 - amount)));
            loc = new RelativeLoc(modX, 0, modZ);
        } else {
            double modX = 0.5 + ((((double) to.getDirection().getModX()) * (amount - 0.5)));
            double modZ = 0.5 + ((((double) to.getDirection().getModZ()) * (amount - 0.5)));
            loc = new RelativeLoc(modX, 0, modZ);
        }
        if (to.isUpper())
            loc.setY(amount);
        else if (from.isUpper())
            loc.setY(1.0 - amount);
        return loc;
    }
}
