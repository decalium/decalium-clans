package org.bukkit.craftbukkit.v1_17_R1.block;

import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import org.bukkit.Material;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;

public class CraftBlastFurnace extends CraftFurnace<BlastFurnaceBlockEntity> implements BlastFurnace {

    public CraftBlastFurnace(Block block) {
        super(block, BlastFurnaceBlockEntity.class);
    }

    public CraftBlastFurnace(Material material, BlastFurnaceBlockEntity te) {
        super(material, te);
    }
}
