/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_17_R1.block.impl;

public final class CraftEnderPortalFrame extends org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData implements org.bukkit.block.data.type.EndPortalFrame, org.bukkit.block.data.Directional {

    public CraftEnderPortalFrame() {
        super();
    }

    public CraftEnderPortalFrame(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.type.CraftEndPortalFrame

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty EYE = getBoolean(net.minecraft.world.level.block.EndPortalFrameBlock.class, "eye");

    @Override
    public boolean hasEye() {
        return get(CraftEnderPortalFrame.EYE);
    }

    @Override
    public void setEye(boolean eye) {
        set(CraftEnderPortalFrame.EYE, eye);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftDirectional

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> FACING = getEnum(net.minecraft.world.level.block.EndPortalFrameBlock.class, "facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(CraftEnderPortalFrame.FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(CraftEnderPortalFrame.FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(CraftEnderPortalFrame.FACING, org.bukkit.block.BlockFace.class);
    }
}
