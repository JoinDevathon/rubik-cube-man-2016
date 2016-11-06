package org.devathon.contest2016.config;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class Utils{

    public static void saveLocation(ConfigurationSection section, Location loc, String path){
        section.set(StringUtils.isEmpty(path) ? "X" : path + ".X", loc.getX());
        section.set(StringUtils.isEmpty(path) ? "Y" : path + ".Y", loc.getY());
        section.set(StringUtils.isEmpty(path) ? "Z" : path + ".Z", loc.getZ());
        section.set(StringUtils.isEmpty(path) ? "Pitch" : path + ".Pitch", loc.getPitch());
        section.set(StringUtils.isEmpty(path) ? "Yaw" : path + ".Yaw", loc.getYaw());
        section.set(StringUtils.isEmpty(path) ? "World" : path + ".World", loc.getWorld().getName());
    }

    public static Location loadLocation(ConfigurationSection section, String path){
        String worldName = section.getString(StringUtils.isEmpty(path) ? "World" : path + ".World");
        World world = Bukkit.getWorld(worldName);
        if (world != null){
            return new Location(world, section.getDouble(StringUtils.isEmpty(path) ? "X" : path + ".X"),
                    section.getDouble(StringUtils.isEmpty(path) ? "Y" : path + ".Y"),
                    section.getDouble(StringUtils.isEmpty(path) ? "Z" : path + ".Z"),
                    section.getInt(StringUtils.isEmpty(path) ? "Yaw" : path + ".Yaw"),
                    section.getInt(StringUtils.isEmpty(path) ? "Pitch" : path + ".Pitch"));
        }
        return null;
    }
}
