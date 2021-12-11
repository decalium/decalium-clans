package org.bukkit.craftbukkit.v1_18_R1.structure;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlockStates;
import org.bukkit.structure.Palette;

public class CraftPalette implements Palette {

    private final StructureTemplate.Palette palette;

    public CraftPalette(StructureTemplate.Palette palette) {
        this.palette = palette;
    }

    @Override
    public List<BlockState> getBlocks() {
        List<BlockState> blocks = new ArrayList<>();
        for (StructureTemplate.StructureBlockInfo blockInfo : this.palette.blocks()) {
            blocks.add(CraftBlockStates.getBlockState(blockInfo.pos, blockInfo.state, blockInfo.nbt));
        }
        return blocks;
    }

    @Override
    public int getBlockCount() {
        return this.palette.blocks().size();
    }
}
