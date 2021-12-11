package org.bukkit.craftbukkit.v1_18_R1.block.data.type;

import org.bukkit.block.data.type.BubbleColumn;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public abstract class CraftBubbleColumn extends CraftBlockData implements BubbleColumn {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty DRAG = getBoolean("drag");

    @Override
    public boolean isDrag() {
        return get(CraftBubbleColumn.DRAG);
    }

    @Override
    public void setDrag(boolean drag) {
        set(CraftBubbleColumn.DRAG, drag);
    }
}
