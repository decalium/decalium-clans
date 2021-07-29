package org.bukkit.craftbukkit.v1_17_R1.block;

import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.bukkit.Material;
import org.bukkit.block.Bell;
import org.bukkit.block.Block;

public class CraftBell extends CraftBlockEntityState<BellBlockEntity> implements Bell {

    public CraftBell(Block block) {
        super(block, BellBlockEntity.class);
    }

    public CraftBell(Material material, BellBlockEntity te) {
        super(material, te);
    }
}
