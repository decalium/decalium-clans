/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_17_R1.block.impl;

public final class CraftTorchWall extends org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData implements org.bukkit.block.data.Directional {

    public CraftTorchWall() {
        super();
    }

    public CraftTorchWall(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftDirectional

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> FACING = getEnum(net.minecraft.world.level.block.WallTorchBlock.class, "facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(CraftTorchWall.FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(CraftTorchWall.FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(CraftTorchWall.FACING, org.bukkit.block.BlockFace.class);
    }
}
