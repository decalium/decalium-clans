package org.bukkit.craftbukkit.v1_18_R1.block.data;

import org.bukkit.block.data.Snowable;

public abstract class CraftSnowable extends CraftBlockData implements Snowable {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty SNOWY = getBoolean("snowy");

    @Override
    public boolean isSnowy() {
        return get(CraftSnowable.SNOWY);
    }

    @Override
    public void setSnowy(boolean snowy) {
        set(CraftSnowable.SNOWY, snowy);
    }
}
