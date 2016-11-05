package org.devathon.contest2016.items;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.devathon.contest2016.HoldingMachine;
import org.devathon.contest2016.MachinePart;
import org.devathon.contest2016.rails.Rail;
import org.devathon.contest2016.rails.RailConnector;
import org.devathon.contest2016.util.RelativeLoc;
import org.devathon.contest2016.util.Teleport;

public class MovingItem{

    private ArmorStand stand;
    private Item item;
    private double amountThrough = 0;
    private double velocity = 0.05;
    private Rail on;
    private RailConnector from, to;
    private boolean exists = true;

    public MovingItem(ItemStack item, Rail on, RailConnector startConnector){
        this(item, on, startConnector, on.getConnector(startConnector));
    }

    public MovingItem(ItemStack item, Rail on, RailConnector startConnector, RailConnector otherConnector){
        this.from = startConnector;
        this.to = otherConnector;
        this.on = on;
        Location start = getLocation();
        this.stand = (ArmorStand) start.getWorld().spawnEntity(start, EntityType.ARMOR_STAND);
        this.item = start.getWorld().dropItem(start, item);
        this.item.setPickupDelay(Integer.MAX_VALUE);
        this.stand.setVisible(false);
        this.stand.setPassenger(this.item);
        this.stand.setGravity(false);
        ItemHandler.getInstance().addItem(this);
    }

    public void move(){
        amountThrough += velocity;
        while (amountThrough >= 1.0 && exists){
            amountThrough -= 1.0;
            moveOn();
        }
        if (exists)
            updateLocation();
    }

    private void updateLocation(){
        Location loc = getLocation();
        Teleport.teleport(stand, loc);
    }

    private void moveOn(){
        MachinePart part = on.getRelative(to);
        if (part == null){
            remove(false);
            exists = false;
        } else if (part instanceof Rail){
            on = (Rail) part;
            RailConnector[] connectors = on.getConnections();
            int index = 0;
            for (int i = 0; i < connectors.length; i++){
                RailConnector connector = connectors[i];
                if (connector.getDirection().getOppositeFace() == to.getDirection()){
                    this.from = connector;
                    index = i;
                    break;
                }
            }
            if (this.from == null)
                this.from = connectors[0];
            this.to = connectors[index == 0 ? 1 : 0];
        } else if (part instanceof HoldingMachine){
            HoldingMachine machine = (HoldingMachine) part;
            boolean accepted = machine.acceptItem(this);
            remove(accepted);
            exists = false;
        } else {
            remove(false);
            exists = false;
        }
    }

    private Location getLocation(){
        RelativeLoc relative = RailConnector.getPosInBlock(from, to, amountThrough);
        return relative.apply(on.getLocation()).add(0, -1.35, 0);
    }

    public void remove(boolean itemAndStand){
        ItemHandler.getInstance().removeItem(this, itemAndStand);
    }

    protected void removeItem(boolean itemAndStand){
        stand.remove();
        if (itemAndStand)
            item.remove();
        else
            item.setPickupDelay(1);
    }
}
