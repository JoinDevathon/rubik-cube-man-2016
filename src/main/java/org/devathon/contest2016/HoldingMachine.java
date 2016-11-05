package org.devathon.contest2016;

import org.devathon.contest2016.items.MovingItem;

public interface HoldingMachine extends MachinePart{

    boolean acceptItem(MovingItem item);
}
