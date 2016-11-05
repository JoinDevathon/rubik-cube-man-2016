package org.devathon.contest2016.util;

import org.bukkit.Location;

public class RelativeLoc{

    private double x, y, z;

    public RelativeLoc(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getZ(){
        return z;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    public void setZ(double z){
        this.z = z;
    }

    public Location apply(Location loc){
        return loc.clone().add(x, y, z);
    }
}
