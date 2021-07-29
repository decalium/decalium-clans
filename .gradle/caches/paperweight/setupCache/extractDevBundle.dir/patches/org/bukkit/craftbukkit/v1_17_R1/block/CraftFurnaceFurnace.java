package org.bukkit.craftbukkit.v1_17_R1.block;

import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CraftFurnaceFurnace extends CraftFurnace<FurnaceBlockEntity> {

    public CraftFurnaceFurnace(Block block) {
        super(block, FurnaceBlockEntity.class);
    }

    public CraftFurnaceFurnace(Material material, FurnaceBlockEntity te) {
        super(material, te);
    }
}
