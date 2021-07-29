package org.bukkit.craftbukkit.v1_17_R1.util;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;

public class DummyGeneratorAccess implements LevelAccessor {

    public static final LevelAccessor INSTANCE = new DummyGeneratorAccess();

    protected DummyGeneratorAccess() {
    }

    @Override
    public TickList<Block> getBlockTicks() {
        return EmptyTickList.empty();
    }

    @Override
    public TickList<Fluid> getLiquidTicks() {
        return EmptyTickList.empty();
    }

    @Override
    public LevelData getLevelData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MinecraftServer getServer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkSource getChunkSource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Random getRandom() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(Player player, BlockPos pos, SoundEvent sound, SoundSource category, float volume, float pitch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addParticle(ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void levelEvent(Player player, int eventId, BlockPos pos, int data) {
        // Used by PowderSnowBlock.removeFluid
    }

    @Override
    public void gameEvent(Entity entity, GameEvent event, BlockPos pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServerLevel getMinecraftWorld() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RegistryAccess registryAccess() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Entity> getEntities(Entity except, AABB box, Predicate<? super Entity> predicate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> filter, AABB box, Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<? extends Player> players() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHeight(Heightmap.Types heightmap, int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSkyDarken() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BiomeManager getBiomeManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Biome getUncachedNoiseBiome(int biomeX, int biomeY, int biomeZ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DimensionType dimensionType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float getShade(Direction direction, boolean shaded) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LevelLightEngine getLightEngine() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.defaultBlockState(); // SPIGOT-6515
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState(); // SPIGOT-6634
    }
    // Paper start - if loaded util
    @javax.annotation.Nullable
    @Override
    public ChunkAccess getChunkIfLoadedImmediately(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlockState getTypeIfLoaded(BlockPos blockposition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FluidState getFluidIfLoaded(BlockPos blockposition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    // Paper end
    @Override
    public WorldBorder getWorldBorder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isFluidAtPosition(BlockPos pos, Predicate<FluidState> state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        return false;
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth) {
        return false; // SPIGOT-6515
    }
}
