/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_18_R1.block.impl;

public final class CraftRepeater extends org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData implements org.bukkit.block.data.type.Repeater, org.bukkit.block.data.Directional, org.bukkit.block.data.Powerable {

    public CraftRepeater() {
        super();
    }

    public CraftRepeater(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_18_R1.block.data.type.CraftRepeater

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty DELAY = getInteger(net.minecraft.world.level.block.RepeaterBlock.class, "delay");
    private static final net.minecraft.world.level.block.state.properties.BooleanProperty LOCKED = getBoolean(net.minecraft.world.level.block.RepeaterBlock.class, "locked");

    @Override
    public int getDelay() {
        return get(CraftRepeater.DELAY);
    }

    @Override
    public void setDelay(int delay) {
        set(CraftRepeater.DELAY, delay);
    }

    @Override
    public int getMinimumDelay() {
        return getMin(CraftRepeater.DELAY);
    }

    @Override
    public int getMaximumDelay() {
        return getMax(CraftRepeater.DELAY);
    }

    @Override
    public boolean isLocked() {
        return get(CraftRepeater.LOCKED);
    }

    @Override
    public void setLocked(boolean locked) {
        set(CraftRepeater.LOCKED, locked);
    }

    // org.bukkit.craftbukkit.v1_18_R1.block.data.CraftDirectional

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> FACING = getEnum(net.minecraft.world.level.block.RepeaterBlock.class, "facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(CraftRepeater.FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(CraftRepeater.FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(CraftRepeater.FACING, org.bukkit.block.BlockFace.class);
    }

    // org.bukkit.craftbukkit.v1_18_R1.block.data.CraftPowerable

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty POWERED = getBoolean(net.minecraft.world.level.block.RepeaterBlock.class, "powered");

    @Override
    public boolean isPowered() {
        return get(CraftRepeater.POWERED);
    }

    @Override
    public void setPowered(boolean powered) {
        set(CraftRepeater.POWERED, powered);
    }
}
