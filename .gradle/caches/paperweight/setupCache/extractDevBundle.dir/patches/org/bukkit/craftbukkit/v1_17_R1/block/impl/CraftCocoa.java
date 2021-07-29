/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_17_R1.block.impl;

public final class CraftCocoa extends org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData implements org.bukkit.block.data.type.Cocoa, org.bukkit.block.data.Ageable, org.bukkit.block.data.Directional {

    public CraftCocoa() {
        super();
    }

    public CraftCocoa(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftAgeable

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty AGE = getInteger(net.minecraft.world.level.block.CocoaBlock.class, "age");

    @Override
    public int getAge() {
        return get(CraftCocoa.AGE);
    }

    @Override
    public void setAge(int age) {
        set(CraftCocoa.AGE, age);
    }

    @Override
    public int getMaximumAge() {
        return getMax(CraftCocoa.AGE);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftDirectional

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> FACING = getEnum(net.minecraft.world.level.block.CocoaBlock.class, "facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(CraftCocoa.FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(CraftCocoa.FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(CraftCocoa.FACING, org.bukkit.block.BlockFace.class);
    }
}
