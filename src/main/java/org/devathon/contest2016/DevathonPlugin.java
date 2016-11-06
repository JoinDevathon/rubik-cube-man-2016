package org.devathon.contest2016;

import org.bukkit.plugin.java.JavaPlugin;
import org.devathon.contest2016.items.ItemHandler;
import org.devathon.contest2016.rails.RailHandler;

public class DevathonPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new MachineHandler(this);
        new RailHandler(this);
        new ItemHandler(this);
    }

    @Override
    public void onDisable() {
        // put your disable code here
    }
}

