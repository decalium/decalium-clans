/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_18_R1.block.impl;

public final class CraftSoil extends org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData implements org.bukkit.block.data.type.Farmland {

    public CraftSoil() {
        super();
    }

    public CraftSoil(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_18_R1.block.data.type.CraftFarmland

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty MOISTURE = getInteger(net.minecraft.world.level.block.FarmBlock.class, "moisture");

    @Override
    public int getMoisture() {
        return get(CraftSoil.MOISTURE);
    }

    @Override
    public void setMoisture(int moisture) {
        set(CraftSoil.MOISTURE, moisture);
    }

    @Override
    public int getMaximumMoisture() {
        return getMax(CraftSoil.MOISTURE);
    }
}
