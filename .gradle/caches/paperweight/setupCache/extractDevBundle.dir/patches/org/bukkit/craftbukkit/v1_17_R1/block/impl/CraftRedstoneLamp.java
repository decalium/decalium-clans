/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_17_R1.block.impl;

public final class CraftRedstoneLamp extends org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData implements org.bukkit.block.data.Lightable {

    public CraftRedstoneLamp() {
        super();
    }

    public CraftRedstoneLamp(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_17_R1.block.data.CraftLightable

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty LIT = getBoolean(net.minecraft.world.level.block.RedstoneLampBlock.class, "lit");

    @Override
    public boolean isLit() {
        return get(CraftRedstoneLamp.LIT);
    }

    @Override
    public void setLit(boolean lit) {
        set(CraftRedstoneLamp.LIT, lit);
    }
}
