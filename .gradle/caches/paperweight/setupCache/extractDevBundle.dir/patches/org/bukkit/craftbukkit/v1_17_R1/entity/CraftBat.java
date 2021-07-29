package org.bukkit.craftbukkit.v1_17_R1.entity;

import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;

public class CraftBat extends CraftAmbient implements Bat {
    public CraftBat(CraftServer server, net.minecraft.world.entity.ambient.Bat entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.ambient.Bat getHandle() {
        return (net.minecraft.world.entity.ambient.Bat) entity;
    }

    @Override
    public String toString() {
        return "CraftBat";
    }

    @Override
    public EntityType getType() {
        return EntityType.BAT;
    }

    @Override
    public boolean isAwake() {
        return !this.getHandle().isResting();
    }

    @Override
    public void setAwake(boolean state) {
        this.getHandle().setResting(!state);
    }
}
