package org.bukkit.craftbukkit.v1_18_R1.block.data;

import org.bukkit.block.data.Waterlogged;

public abstract class CraftWaterlogged extends CraftBlockData implements Waterlogged {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean("waterlogged");

    @Override
    public boolean isWaterlogged() {
        return get(CraftWaterlogged.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(CraftWaterlogged.WATERLOGGED, waterlogged);
    }
}
