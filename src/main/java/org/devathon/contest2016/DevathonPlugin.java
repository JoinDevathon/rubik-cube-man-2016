package org.devathon.contest2016;

import org.bukkit.plugin.java.JavaPlugin;
import org.devathon.contest2016.containers.BlockChecker;

public class DevathonPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new BlockChecker(this);
    }

    @Override
    public void onDisable() {
        // put your disable code here
    }
}

