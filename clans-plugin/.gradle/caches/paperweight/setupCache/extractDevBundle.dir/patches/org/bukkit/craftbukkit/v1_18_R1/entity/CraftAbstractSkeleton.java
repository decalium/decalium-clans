package org.bukkit.craftbukkit.v1_18_R1.entity;

import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.Skeleton;

public abstract class CraftAbstractSkeleton extends CraftMonster implements AbstractSkeleton, com.destroystokyo.paper.entity.CraftRangedEntity<net.minecraft.world.entity.monster.AbstractSkeleton> { // Paper

    public CraftAbstractSkeleton(CraftServer server, net.minecraft.world.entity.monster.AbstractSkeleton entity) {
        super(server, entity);
    }

    @Override
    public void setSkeletonType(Skeleton.SkeletonType type) {
        throw new UnsupportedOperationException("Not supported.");
    }
	// Paper start
    @Override
    public net.minecraft.world.entity.monster.AbstractSkeleton getHandle() {
        return (net.minecraft.world.entity.monster.AbstractSkeleton) super.getHandle();
    }
    // Paper end
}