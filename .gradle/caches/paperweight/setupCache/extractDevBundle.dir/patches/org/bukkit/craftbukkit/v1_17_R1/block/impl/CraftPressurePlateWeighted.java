/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_17_R1.block.impl;

public final class CraftPressurePlateWeighted extends org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData implements org.bukkit.block.data.AnaloguePowerable {

    public CraftPressurePlateWeighted() {
        super();
    }

    public CraftPressurePlateWeighted(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftAnaloguePowerable

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty POWER = getInteger(net.minecraft.world.level.block.WeightedPressurePlateBlock.class, "power");

    @Override
    public int getPower() {
        return get(CraftPressurePlateWeighted.POWER);
    }

    @Override
    public void setPower(int power) {
        set(CraftPressurePlateWeighted.POWER, power);
    }

    @Override
    public int getMaximumPower() {
        return getMax(CraftPressurePlateWeighted.POWER);
    }
}
