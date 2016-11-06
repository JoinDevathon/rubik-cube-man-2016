package org.devathon.contest2016.rails;

import org.bukkit.block.Block;

public class PoweredRail extends Rail{

    public PoweredRail(Block block){
        super(block);
    }

    @Override
    public double calculateVelocity(double velocity){
        Block block = getBlock();
        org.bukkit.material.PoweredRail rail = (org.bukkit.material.PoweredRail) block.getState().getData();
        if (rail.isPowered())
            return velocity + 0.01;
        return velocity - 0.02;
    }
}
