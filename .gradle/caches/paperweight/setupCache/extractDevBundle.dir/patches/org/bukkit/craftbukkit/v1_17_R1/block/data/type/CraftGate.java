package org.bukkit.craftbukkit.v1_17_R1.block.data.type;

import org.bukkit.block.data.type.Gate;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;

public abstract class CraftGate extends CraftBlockData implements Gate {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty IN_WALL = getBoolean("in_wall");

    @Override
    public boolean isInWall() {
        return get(CraftGate.IN_WALL);
    }

    @Override
    public void setInWall(boolean inWall) {
        set(CraftGate.IN_WALL, inWall);
    }
}
