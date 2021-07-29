/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_17_R1.block.impl;

public final class CraftBeehive extends org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData implements org.bukkit.block.data.type.Beehive, org.bukkit.block.data.Directional {

    public CraftBeehive() {
        super();
    }

    public CraftBeehive(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.type.CraftBeehive

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty HONEY_LEVEL = getInteger(net.minecraft.world.level.block.BeehiveBlock.class, "honey_level");

    @Override
    public int getHoneyLevel() {
        return get(CraftBeehive.HONEY_LEVEL);
    }

    @Override
    public void setHoneyLevel(int honeyLevel) {
        set(CraftBeehive.HONEY_LEVEL, honeyLevel);
    }

    @Override
    public int getMaximumHoneyLevel() {
        return getMax(CraftBeehive.HONEY_LEVEL);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftDirectional

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> FACING = getEnum(net.minecraft.world.level.block.BeehiveBlock.class, "facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(CraftBeehive.FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(CraftBeehive.FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(CraftBeehive.FACING, org.bukkit.block.BlockFace.class);
    }
}
