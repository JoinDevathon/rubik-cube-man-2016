package org.devathon.contest2016.containers;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BlockChecker implements Listener{

    public BlockChecker(Plugin plugin){
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

    }
}
