/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_17_R1.block.impl;

public final class CraftStairs extends org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData implements org.bukkit.block.data.type.Stairs, org.bukkit.block.data.Bisected, org.bukkit.block.data.Directional, org.bukkit.block.data.Waterlogged {

    public CraftStairs() {
        super();
    }

    public CraftStairs(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.type.CraftStairs

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> SHAPE = getEnum(net.minecraft.world.level.block.StairBlock.class, "shape");

    @Override
    public org.bukkit.block.data.type.Stairs.Shape getShape() {
        return get(CraftStairs.SHAPE, org.bukkit.block.data.type.Stairs.Shape.class);
    }

    @Override
    public void setShape(org.bukkit.block.data.type.Stairs.Shape shape) {
        set(CraftStairs.SHAPE, shape);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBisected

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> HALF = getEnum(net.minecraft.world.level.block.StairBlock.class, "half");

    @Override
    public org.bukkit.block.data.Bisected.Half getHalf() {
        return get(CraftStairs.HALF, org.bukkit.block.data.Bisected.Half.class);
    }

    @Override
    public void setHalf(org.bukkit.block.data.Bisected.Half half) {
        set(CraftStairs.HALF, half);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftDirectional

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> FACING = getEnum(net.minecraft.world.level.block.StairBlock.class, "facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(CraftStairs.FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(CraftStairs.FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(CraftStairs.FACING, org.bukkit.block.BlockFace.class);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.StairBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return get(CraftStairs.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(CraftStairs.WATERLOGGED, waterlogged);
    }
}
