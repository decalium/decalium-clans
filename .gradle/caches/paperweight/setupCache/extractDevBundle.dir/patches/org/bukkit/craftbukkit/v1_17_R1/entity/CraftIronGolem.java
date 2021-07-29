package org.bukkit.craftbukkit.v1_17_R1.entity;

import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

public class CraftIronGolem extends CraftGolem implements IronGolem {
    public CraftIronGolem(CraftServer server, net.minecraft.world.entity.animal.IronGolem entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.IronGolem getHandle() {
        return (net.minecraft.world.entity.animal.IronGolem) entity;
    }

    @Override
    public String toString() {
        return "CraftIronGolem";
    }

    @Override
    public boolean isPlayerCreated() {
        return this.getHandle().isPlayerCreated();
    }

    @Override
    public void setPlayerCreated(boolean playerCreated) {
        this.getHandle().setPlayerCreated(playerCreated);
    }

    @Override
    public EntityType getType() {
        return EntityType.IRON_GOLEM;
    }
}
