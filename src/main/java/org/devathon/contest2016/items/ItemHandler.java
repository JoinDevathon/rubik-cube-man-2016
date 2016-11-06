package org.devathon.contest2016.items;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.devathon.contest2016.config.ConfigSaver;
import org.devathon.contest2016.rails.Rail;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ItemHandler{

    private static ItemHandler instance;

    public static ItemHandler getInstance(){
        return instance;
    }

    private Set<MovingItem> items = new HashSet<>();

    public ItemHandler(Plugin plugin){
        instance = this;
        startScheduler(plugin);
    }

    private void startScheduler(Plugin plugin){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> new HashSet<>(items).forEach(MovingItem::move), 1L, 1L);
    }

    protected void addItem(MovingItem item){
        items.add(item);
    }

    public void removeItem(MovingItem item, boolean itemAndStand){
        item.removeItem(itemAndStand);
        items.remove(item);
        ConfigSaver.getInstance().removeObject(item);
    }

    public void railBreak(Rail broken){
        Iterator<MovingItem> itr = items.iterator();
        while (itr.hasNext()){
            MovingItem item = itr.next();
            if (item.getRail().equals(broken)){
                itr.remove();
                item.remove(false);
            }
        }
    }
}
