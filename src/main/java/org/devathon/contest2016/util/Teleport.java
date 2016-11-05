package org.devathon.contest2016.util;

import net.minecraft.server.v1_10_R1.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;

public class Teleport{

    public static void teleport(org.bukkit.entity.Entity e, Location loc){
        teleport(((CraftEntity) e).getHandle(), loc);
    }

    private static void teleport(Entity e, Location loc){
        e.setPosition(loc.getX(), loc.getY(), loc.getZ());
    }
}
