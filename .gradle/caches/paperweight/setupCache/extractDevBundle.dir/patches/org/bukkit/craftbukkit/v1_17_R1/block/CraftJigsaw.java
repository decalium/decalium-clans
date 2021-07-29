package org.bukkit.craftbukkit.v1_17_R1.block;

import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jigsaw;

public class CraftJigsaw extends CraftBlockEntityState<JigsawBlockEntity> implements Jigsaw {

    public CraftJigsaw(Block block) {
        super(block, JigsawBlockEntity.class);
    }

    public CraftJigsaw(Material material, JigsawBlockEntity te) {
        super(material, te);
    }
}
