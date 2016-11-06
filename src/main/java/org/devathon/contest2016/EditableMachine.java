package org.devathon.contest2016;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface EditableMachine{

    void open(Player player);

    void click(InventoryClickEvent event);

    void drag(InventoryDragEvent event);
}
