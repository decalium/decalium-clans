package org.bukkit.craftbukkit.v1_18_R1.block.data;

import org.bukkit.block.data.Openable;

public abstract class CraftOpenable extends CraftBlockData implements Openable {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty OPEN = getBoolean("open");

    @Override
    public boolean isOpen() {
        return get(CraftOpenable.OPEN);
    }

    @Override
    public void setOpen(boolean open) {
        set(CraftOpenable.OPEN, open);
    }
}
