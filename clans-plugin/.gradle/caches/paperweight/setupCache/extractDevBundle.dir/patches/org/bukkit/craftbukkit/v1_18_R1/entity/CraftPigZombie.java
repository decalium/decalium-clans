package org.bukkit.craftbukkit.v1_18_R1.entity;

import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;

public class CraftPigZombie extends CraftZombie implements PigZombie {

    public CraftPigZombie(CraftServer server, ZombifiedPiglin entity) {
        super(server, entity);
    }

    @Override
    public int getAnger() {
        return this.getHandle().getRemainingPersistentAngerTime();
    }

    @Override
    public void setAnger(int level) {
        this.getHandle().setRemainingPersistentAngerTime(level);
    }

    @Override
    public void setAngry(boolean angry) {
        this.setAnger(angry ? 400 : 0);
    }

    @Override
    public boolean isAngry() {
        return this.getAnger() > 0;
    }

    @Override
    public ZombifiedPiglin getHandle() {
        return (ZombifiedPiglin) entity;
    }

    @Override
    public String toString() {
        return "CraftPigZombie";
    }

    @Override
    public EntityType getType() {
        return EntityType.ZOMBIFIED_PIGLIN;
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    public int getConversionTime() {
        throw new UnsupportedOperationException("Not supported by this Entity.");
    }

    @Override
    public void setConversionTime(int time) {
        throw new UnsupportedOperationException("Not supported by this Entity.");
    }
}
