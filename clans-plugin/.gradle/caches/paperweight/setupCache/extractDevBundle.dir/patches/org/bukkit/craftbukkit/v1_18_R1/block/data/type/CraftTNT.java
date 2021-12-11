package org.bukkit.craftbukkit.v1_18_R1.block.data.type;

import org.bukkit.block.data.type.TNT;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public abstract class CraftTNT extends CraftBlockData implements TNT {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty UNSTABLE = getBoolean("unstable");

    @Override
    public boolean isUnstable() {
        return get(CraftTNT.UNSTABLE);
    }

    @Override
    public void setUnstable(boolean unstable) {
        set(CraftTNT.UNSTABLE, unstable);
    }
}
