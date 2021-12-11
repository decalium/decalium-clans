/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_18_R1.block.impl;

public final class CraftPoweredRail extends org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData implements org.bukkit.block.data.type.RedstoneRail, org.bukkit.block.data.Powerable, org.bukkit.block.data.Rail, org.bukkit.block.data.Waterlogged {

    public CraftPoweredRail() {
        super();
    }

    public CraftPoweredRail(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_18_R1.block.data.CraftPowerable

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty POWERED = getBoolean(net.minecraft.world.level.block.PoweredRailBlock.class, "powered");

    @Override
    public boolean isPowered() {
        return get(CraftPoweredRail.POWERED);
    }

    @Override
    public void setPowered(boolean powered) {
        set(CraftPoweredRail.POWERED, powered);
    }

    // org.bukkit.craftbukkit.v1_18_R1.block.data.CraftRail

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> SHAPE = getEnum(net.minecraft.world.level.block.PoweredRailBlock.class, "shape");

    @Override
    public org.bukkit.block.data.Rail.Shape getShape() {
        return get(CraftPoweredRail.SHAPE, org.bukkit.block.data.Rail.Shape.class);
    }

    @Override
    public void setShape(org.bukkit.block.data.Rail.Shape shape) {
        set(CraftPoweredRail.SHAPE, shape);
    }

    @Override
    public java.util.Set<org.bukkit.block.data.Rail.Shape> getShapes() {
        return getValues(CraftPoweredRail.SHAPE, org.bukkit.block.data.Rail.Shape.class);
    }

    // org.bukkit.craftbukkit.v1_18_R1.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.PoweredRailBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return get(CraftPoweredRail.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(CraftPoweredRail.WATERLOGGED, waterlogged);
    }
}
