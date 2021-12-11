package org.bukkit.craftbukkit.v1_18_R1.entity;

import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SpectralArrow;

public class CraftSpectralArrow extends CraftArrow implements SpectralArrow {

    public CraftSpectralArrow(CraftServer server, net.minecraft.world.entity.projectile.SpectralArrow entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.projectile.SpectralArrow getHandle() {
        return (net.minecraft.world.entity.projectile.SpectralArrow) entity;
    }

    @Override
    public String toString() {
        return "CraftSpectralArrow";
    }

    @Override
    public EntityType getType() {
        return EntityType.SPECTRAL_ARROW;
    }

    @Override
    public int getGlowingTicks() {
        return this.getHandle().duration;
    }

    @Override
    public void setGlowingTicks(int duration) {
        this.getHandle().duration = duration;
    }
}
