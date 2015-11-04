package com.arzeyt.darkness.towerObject;

import net.minecraft.world.World;

/**
 * Created by Default on 11/4/2015.
 */
public class FakeTower {
    int x;
    int y;
    int z;
    int power;
    World world;

    public FakeTower(int x, int y, int z, int power, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.power = power;
        this.world=world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getPower() {
        return power;
    }

    public World getWorld() { return world;}

    public void setPower(int power){
        this.power=power;
    }
}
