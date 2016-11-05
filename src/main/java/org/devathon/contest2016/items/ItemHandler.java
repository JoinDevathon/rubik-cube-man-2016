package org.devathon.contest2016.items;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ItemHandler{

    public ItemHandler(Plugin plugin){
        startScheduler(plugin);
    }

    private void startScheduler(Plugin plugin){
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

        });
    }
}
