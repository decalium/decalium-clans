package org.bukkit.craftbukkit.v1_17_R1.block.data.type;

import org.bukkit.block.data.type.Jigsaw;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;

public abstract class CraftJigsaw extends CraftBlockData implements Jigsaw {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> ORIENTATION = getEnum("orientation");

    @Override
    public org.bukkit.block.data.type.Jigsaw.Orientation getOrientation() {
        return get(CraftJigsaw.ORIENTATION, org.bukkit.block.data.type.Jigsaw.Orientation.class);
    }

    @Override
    public void setOrientation(org.bukkit.block.data.type.Jigsaw.Orientation orientation) {
        set(CraftJigsaw.ORIENTATION, orientation);
    }
}
