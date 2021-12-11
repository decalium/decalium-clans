package org.bukkit.craftbukkit.v1_18_R1.block.data.type;

import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public abstract class CraftStairs extends CraftBlockData implements Stairs {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> SHAPE = getEnum("shape");

    @Override
    public org.bukkit.block.data.type.Stairs.Shape getShape() {
        return get(CraftStairs.SHAPE, org.bukkit.block.data.type.Stairs.Shape.class);
    }

    @Override
    public void setShape(org.bukkit.block.data.type.Stairs.Shape shape) {
        set(CraftStairs.SHAPE, shape);
    }
}
