package org.devathon.contest2016.items;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.devathon.contest2016.config.SaveableObject;
import org.devathon.contest2016.config.Utils;
import org.devathon.contest2016.machines.HoldingMachine;
import org.devathon.contest2016.machines.MachinePart;
import org.devathon.contest2016.rails.Rail;
import org.devathon.contest2016.rails.RailConnector;
import org.devathon.contest2016.util.RelativeLoc;
import org.devathon.contest2016.util.Teleport;

public class MovingItem implements SaveableObject{

    private static final double MAX_VELOCITY = 0.5;
    private static final double MIN_VELOCITY = 0.05;
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
        this(item, on, startConnector, otherConnector, MIN_VELOCITY, 0);
    }

    public MovingItem(ItemStack item, Rail on, RailConnector startConnector, RailConnector otherConnector, double velocity, double amountThrough){
        this.from = startConnector;
        this.to = otherConnector;
        this.on = on;
        this.amountThrough = amountThrough;
        this.velocity = velocity <= MIN_VELOCITY ? MIN_VELOCITY : (velocity > MAX_VELOCITY ? MAX_VELOCITY : velocity);
        Location start = getLocation();
        if (!start.getChunk().isLoaded())
            start.getChunk().load();
        if (!start.getChunk().isLoaded())
            start.getChunk().load();
        this.stand = (ArmorStand) start.getWorld().spawnEntity(start, EntityType.ARMOR_STAND);
        this.item = start.getWorld().dropItem(start, item);
        this.item.setPickupDelay(Integer.MAX_VALUE);
        this.stand.setVisible(false);
        this.stand.setPassenger(this.item);
        this.stand.setGravity(false);
        this.stand.setRemoveWhenFarAway(false);
        ItemHandler.getInstance().addItem(this);
    }

    public void move(){
        velocity = on.calculateVelocity(velocity);
        velocity = velocity < MIN_VELOCITY ? MIN_VELOCITY : velocity;
        velocity = velocity > MAX_VELOCITY ? MAX_VELOCITY : velocity;
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

    public Item getItem(){
        return item;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovingItem that = (MovingItem) o;
        return stand != null ? stand.equals(that.stand) : that.stand == null;
    }

    @Override
    public int hashCode(){
        return stand != null ? stand.hashCode() : 0;
    }

    @Override
    public void save(ConfigurationSection section){
        if (item == null) return;
        section.set("item", item.getItemStack());
        Utils.saveLocation(section, on.getLocation(), "loc");
        section.set("from", from.toString());
        section.set("to", to.toString());
        section.set("amountThrough", amountThrough);
        section.set("velocity", velocity);
    }

    public static MovingItem load(ConfigurationSection section){
        Location loc = Utils.loadLocation(section, "loc");
        if (loc == null)
            return null;
        if (!loc.getChunk().isLoaded())
            loc.getChunk().load();
        MachinePart part = MachinePart.partFromBlock(loc.getBlock());
        if (part instanceof Rail){
            ItemStack item = section.getItemStack("item");
            RailConnector from = RailConnector.valueOf(section.getString("from"));
            RailConnector to = RailConnector.valueOf(section.getString("to"));
            double amountThrough = section.getDouble("amountThrough");
            double velocity = section.getDouble("velocity");
            return new MovingItem(item, (Rail) part, from, to, velocity, amountThrough);
        }
        return null;
    }
}
