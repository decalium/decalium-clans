package org.bukkit.craftbukkit.v1_18_R1.entity;

import net.minecraft.world.entity.animal.Pufferfish;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PufferFish;

public class CraftPufferFish extends CraftFish implements PufferFish {

    public CraftPufferFish(CraftServer server, Pufferfish entity) {
        super(server, entity);
    }

    @Override
    public Pufferfish getHandle() {
        return (Pufferfish) super.getHandle();
    }

    @Override
    public int getPuffState() {
        return this.getHandle().getPuffState();
    }

    @Override
    public void setPuffState(int state) {
        this.getHandle().setPuffState(state);
    }

    @Override
    public String toString() {
        return "CraftPufferFish";
    }

    @Override
    public EntityType getType() {
        return EntityType.PUFFERFISH;
    }
}
