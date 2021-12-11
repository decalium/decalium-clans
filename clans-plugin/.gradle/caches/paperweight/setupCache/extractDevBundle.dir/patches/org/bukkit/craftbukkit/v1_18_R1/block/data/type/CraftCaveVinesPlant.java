package org.bukkit.craftbukkit.v1_18_R1.block.data.type;

import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public abstract class CraftCaveVinesPlant extends CraftBlockData implements CaveVinesPlant {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty BERRIES = getBoolean("berries");

    @Override
    public boolean isBerries() {
        return get(CraftCaveVinesPlant.BERRIES);
    }

    @Override
    public void setBerries(boolean berries) {
        set(CraftCaveVinesPlant.BERRIES, berries);
    }
}
