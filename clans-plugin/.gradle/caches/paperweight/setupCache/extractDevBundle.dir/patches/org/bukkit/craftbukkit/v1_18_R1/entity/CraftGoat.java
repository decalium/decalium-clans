package org.bukkit.craftbukkit.v1_18_R1.entity;

import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Goat;

public class CraftGoat extends CraftAnimals implements Goat {

    public CraftGoat(CraftServer server, net.minecraft.world.entity.animal.goat.Goat entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.goat.Goat getHandle() {
        return (net.minecraft.world.entity.animal.goat.Goat) super.getHandle();
    }

    @Override
    public EntityType getType() {
        return EntityType.GOAT;
    }

    @Override
    public String toString() {
        return "CraftGoat";
    }

    @Override
    public boolean isScreaming() {
        return this.getHandle().isScreamingGoat();
    }

    @Override
    public void setScreaming(boolean screaming) {
        this.getHandle().setScreamingGoat(screaming);
    }

    // Paper start - Goat ram API
    @Override
    public void ram(@org.jetbrains.annotations.NotNull org.bukkit.entity.LivingEntity entity) {
        this.getHandle().ram(((CraftLivingEntity) entity).getHandle());
    }
    // Paper end
}
