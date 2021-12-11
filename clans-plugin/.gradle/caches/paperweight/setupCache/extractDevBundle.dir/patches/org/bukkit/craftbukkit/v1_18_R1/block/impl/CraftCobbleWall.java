/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_18_R1.block.impl;

public final class CraftCobbleWall extends org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData implements org.bukkit.block.data.type.Wall, org.bukkit.block.data.Waterlogged {

    public CraftCobbleWall() {
        super();
    }

    public CraftCobbleWall(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_18_R1.block.data.type.CraftWall

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty UP = getBoolean(net.minecraft.world.level.block.WallBlock.class, "up");
    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?>[] HEIGHTS = new net.minecraft.world.level.block.state.properties.EnumProperty[]{
        getEnum(net.minecraft.world.level.block.WallBlock.class, "north"), getEnum(net.minecraft.world.level.block.WallBlock.class, "east"), getEnum(net.minecraft.world.level.block.WallBlock.class, "south"), getEnum(net.minecraft.world.level.block.WallBlock.class, "west")
    };

    @Override
    public boolean isUp() {
        return get(CraftCobbleWall.UP);
    }

    @Override
    public void setUp(boolean up) {
        set(CraftCobbleWall.UP, up);
    }

    @Override
    public org.bukkit.block.data.type.Wall.Height getHeight(org.bukkit.block.BlockFace face) {
        return get(CraftCobbleWall.HEIGHTS[face.ordinal()], org.bukkit.block.data.type.Wall.Height.class);
    }

    @Override
    public void setHeight(org.bukkit.block.BlockFace face, org.bukkit.block.data.type.Wall.Height height) {
        set(CraftCobbleWall.HEIGHTS[face.ordinal()], height);
    }

    // org.bukkit.craftbukkit.v1_18_R1.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.WallBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return get(CraftCobbleWall.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(CraftCobbleWall.WATERLOGGED, waterlogged);
    }
}
