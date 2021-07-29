package org.bukkit.craftbukkit.v1_17_R1.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlockState;

public class BlockStateListPopulator extends DummyGeneratorAccess {
    private final Level world;
    private final LinkedHashMap<BlockPos, CraftBlockState> list;

    public BlockStateListPopulator(Level world) {
        this(world, new LinkedHashMap<>());
    }

    public BlockStateListPopulator(Level world, LinkedHashMap<BlockPos, CraftBlockState> list) {
        this.world = world;
        this.list = list;
    }

    @Override
    public net.minecraft.world.level.block.state.BlockState getBlockState(BlockPos pos) {
        CraftBlockState state = this.list.get(pos);
        return (state != null) ? state.getHandle() : this.world.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        CraftBlockState state = this.list.get(pos);
        return (state != null) ? state.getHandle().getFluidState() : this.world.getFluidState(pos);
    }

    @Override
    public boolean setBlock(BlockPos pos, net.minecraft.world.level.block.state.BlockState state, int flags) {
        CraftBlockState state1 = CraftBlockState.getBlockState(world, pos, flags);
        state1.setData(state);
        // remove first to keep insertion order
        this.list.remove(pos);
        this.list.put(pos.immutable(), state1);
        return true;
    }

    public void updateList() {
        for (BlockState state : this.list.values()) {
            state.update(true);
        }
    }

    public Set<BlockPos> getBlocks() {
        return this.list.keySet();
    }

    public List<CraftBlockState> getList() {
        return new ArrayList<>(this.list.values());
    }

    public Level getWorld() {
        return this.world;
    }
}
