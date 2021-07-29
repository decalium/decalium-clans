package org.bukkit.craftbukkit.v1_17_R1.entity;

import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PolarBear;

public class CraftPolarBear extends CraftAnimals implements PolarBear {

    public CraftPolarBear(CraftServer server, net.minecraft.world.entity.animal.PolarBear entity) {
        super(server, entity);
    }
    @Override
    public net.minecraft.world.entity.animal.PolarBear getHandle() {
        return (net.minecraft.world.entity.animal.PolarBear) entity;
    }

    @Override
    public String toString() {
        return "CraftPolarBear";
    }

    @Override
    public EntityType getType() {
        return EntityType.POLAR_BEAR;
    }
}
