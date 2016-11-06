package org.devathon.contest2016.rails;

import org.bukkit.block.Block;

public class NormalRail extends Rail{

    public NormalRail(Block block){
        super(block);
    }

    @Override
    public double calculateVelocity(double velocity){
        return velocity - 0.01;
    }
}
