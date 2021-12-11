package org.bukkit.craftbukkit.v1_18_R1.block.data.type;

import org.bukkit.block.data.type.Bamboo;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public abstract class CraftBamboo extends CraftBlockData implements Bamboo {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> LEAVES = getEnum("leaves");

    @Override
    public org.bukkit.block.data.type.Bamboo.Leaves getLeaves() {
        return get(CraftBamboo.LEAVES, org.bukkit.block.data.type.Bamboo.Leaves.class);
    }

    @Override
    public void setLeaves(org.bukkit.block.data.type.Bamboo.Leaves leaves) {
        set(CraftBamboo.LEAVES, leaves);
    }
}
