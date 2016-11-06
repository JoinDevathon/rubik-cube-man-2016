package org.devathon.contest2016.machines;

import org.bukkit.inventory.ItemStack;
import org.devathon.contest2016.items.MovingItem;

public interface HoldingMachine extends MachinePart{

    boolean acceptItem(MovingItem item);

    ItemStack[] pullAndRemove();
}
