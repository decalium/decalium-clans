package org.bukkit.craftbukkit.v1_18_R1.block.data.type;

import org.bukkit.block.data.type.DaylightDetector;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public abstract class CraftDaylightDetector extends CraftBlockData implements DaylightDetector {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty INVERTED = getBoolean("inverted");

    @Override
    public boolean isInverted() {
        return get(CraftDaylightDetector.INVERTED);
    }

    @Override
    public void setInverted(boolean inverted) {
        set(CraftDaylightDetector.INVERTED, inverted);
    }
}
