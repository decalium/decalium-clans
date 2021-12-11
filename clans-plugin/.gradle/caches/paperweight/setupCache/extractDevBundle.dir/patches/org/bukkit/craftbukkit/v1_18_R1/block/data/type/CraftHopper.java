package org.bukkit.craftbukkit.v1_18_R1.block.data.type;

import org.bukkit.block.data.type.Hopper;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public abstract class CraftHopper extends CraftBlockData implements Hopper {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty ENABLED = getBoolean("enabled");

    @Override
    public boolean isEnabled() {
        return get(CraftHopper.ENABLED);
    }

    @Override
    public void setEnabled(boolean enabled) {
        set(CraftHopper.ENABLED, enabled);
    }
}
