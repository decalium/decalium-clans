package org.bukkit.craftbukkit.v1_17_R1.block.data.type;

import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;

public abstract class CraftSeaPickle extends CraftBlockData implements SeaPickle {

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty PICKLES = getInteger("pickles");

    @Override
    public int getPickles() {
        return get(CraftSeaPickle.PICKLES);
    }

    @Override
    public void setPickles(int pickles) {
        set(CraftSeaPickle.PICKLES, pickles);
    }

    @Override
    public int getMinimumPickles() {
        return getMin(CraftSeaPickle.PICKLES);
    }

    @Override
    public int getMaximumPickles() {
        return getMax(CraftSeaPickle.PICKLES);
    }
}
