/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_17_R1.block.impl;

public final class CraftCauldron extends org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData implements org.bukkit.block.data.Levelled {

    public CraftCauldron() {
        super();
    }

    public CraftCauldron(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftLevelled

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty LEVEL = getInteger(net.minecraft.world.level.block.CauldronBlock.class, "level");

    @Override
    public int getLevel() {
        return get(CraftCauldron.LEVEL);
    }

    @Override
    public void setLevel(int level) {
        set(CraftCauldron.LEVEL, level);
    }

    @Override
    public int getMaximumLevel() {
        return getMax(CraftCauldron.LEVEL);
    }
}
