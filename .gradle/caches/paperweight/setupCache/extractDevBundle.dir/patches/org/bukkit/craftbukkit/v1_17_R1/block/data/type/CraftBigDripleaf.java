package org.bukkit.craftbukkit.v1_17_R1.block.data.type;

import org.bukkit.block.data.type.BigDripleaf;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;

public abstract class CraftBigDripleaf extends CraftBlockData implements BigDripleaf {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> TILT = getEnum("tilt");

    @Override
    public Tilt getTilt() {
        return get(CraftBigDripleaf.TILT, org.bukkit.block.data.type.BigDripleaf.Tilt.class);
    }

    @Override
    public void setTilt(org.bukkit.block.data.type.BigDripleaf.Tilt tilt) {
        set(CraftBigDripleaf.TILT, tilt);
    }
}
