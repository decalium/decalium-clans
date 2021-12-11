package org.bukkit.craftbukkit.v1_18_R1.block;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType; // Paper
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftMagicNumbers;

public final class CraftBlockStates {

    private abstract static class BlockStateFactory<B extends CraftBlockState> {

        public final Class<B> blockStateType;

        public BlockStateFactory(Class<B> blockStateType) {
            this.blockStateType = blockStateType;
        }

        // The given world can be null for unplaced BlockStates.
        // If the world is not null and the given block data is a tile entity, the given tile entity is expected to not be null.
        // Otherwise, the given tile entity may or may not be null.
        // If the given tile entity is not null, its position and block data are expected to match the given block position and block data.
        // In some situations, such as during chunk generation, the tile entity's world may be null, even if the given world is not null.
        // If the tile entity's world is not null, it is expected to match the given world.
        public abstract B createBlockState(World world, BlockPos blockPosition, net.minecraft.world.level.block.state.BlockState blockData, BlockEntity tileEntity);
    }

    private static class BlockEntityStateFactory<T extends BlockEntity, B extends CraftBlockEntityState<T>> extends BlockStateFactory<B> {

        private final BiFunction<World, T, B> blockStateConstructor;
        private final BiFunction<BlockPos, net.minecraft.world.level.block.state.BlockState, T> tileEntityConstructor;

        protected BlockEntityStateFactory(Class<B> blockStateType, BiFunction<World, T, B> blockStateConstructor, BiFunction<BlockPos, net.minecraft.world.level.block.state.BlockState, T> tileEntityConstructor) {
            super(blockStateType);
            this.blockStateConstructor = blockStateConstructor;
            this.tileEntityConstructor = tileEntityConstructor;
        }

        @Override
        public final B createBlockState(World world, BlockPos blockPosition, net.minecraft.world.level.block.state.BlockState blockData, BlockEntity tileEntity) {
            if (world != null) {
                Preconditions.checkState(tileEntity != null, "Tile is null, asynchronous access? %s", CraftBlock.at(((CraftWorld) world).getHandle(), blockPosition));
            } else if (tileEntity == null) {
                tileEntity = this.createTileEntity(blockPosition, blockData);
            }
            return this.createBlockState(world, (T) tileEntity);
        }

        private T createTileEntity(BlockPos blockPosition, net.minecraft.world.level.block.state.BlockState blockData) {
            return this.tileEntityConstructor.apply(blockPosition, blockData);
        }

        private B createBlockState(World world, T tileEntity) {
            return this.blockStateConstructor.apply(world, tileEntity);
        }
    }

    private static final Map<Material, BlockStateFactory<?>> FACTORIES = new HashMap<>();
    private static final BlockStateFactory<?> DEFAULT_FACTORY = new BlockStateFactory<CraftBlockState>(CraftBlockState.class) {
        @Override
        public CraftBlockState createBlockState(World world, BlockPos blockPosition, net.minecraft.world.level.block.state.BlockState blockData, BlockEntity tileEntity) {
            // Paper start - revert revert
            // When a block is being destroyed, the TileEntity may temporarily still exist while the block's type has already been set to AIR. We ignore the TileEntity in this case.
            Preconditions.checkState(tileEntity == null/* || CraftMagicNumbers.getMaterial(blockData.getBlock()) == Material.AIR*/, "Unexpected BlockState for %s", CraftMagicNumbers.getMaterial(blockData.getBlock())); // Paper - don't ignore the TileEntity while its still valid
            // Paper end
            return new CraftBlockState(world, blockPosition, blockData);
        }
    };
    // Paper start
    private static final Map<BlockEntityType<?>, BlockStateFactory<?>> FACTORIES_BY_BLOCK_ENTITY_TYPE = new HashMap<>();
    private static void register(BlockEntityType<?> type, BlockStateFactory<?> factory) {
        FACTORIES_BY_BLOCK_ENTITY_TYPE.put(type, factory);
    }
    // Paper end

    static {
        // Paper start - simplify
        register(BlockEntityType.SIGN, CraftSign.class, CraftSign::new);
        register(BlockEntityType.SKULL, CraftSkull.class, CraftSkull::new);
        register(BlockEntityType.COMMAND_BLOCK, CraftCommandBlock.class, CraftCommandBlock::new);
        register(BlockEntityType.BANNER, CraftBanner.class, CraftBanner::new);
        register(BlockEntityType.SHULKER_BOX, CraftShulkerBox.class, CraftShulkerBox::new);
        register(BlockEntityType.BED, CraftBed.class, CraftBed::new);
        register(BlockEntityType.BEEHIVE, CraftBeehive.class, CraftBeehive::new);
        register(BlockEntityType.CAMPFIRE, CraftCampfire.class, CraftCampfire::new);
        register(BlockEntityType.CHEST, CraftChest.class, CraftChest::new); // Paper - split up chests due to different block entity types
        register(BlockEntityType.TRAPPED_CHEST, CraftChest.class, CraftChest::new); // Paper - split up chests due to different block entity types
        register(BlockEntityType.BARREL, CraftBarrel.class, CraftBarrel::new);
        register(BlockEntityType.BEACON, CraftBeacon.class, CraftBeacon::new);
        register(BlockEntityType.BELL, CraftBell.class, CraftBell::new);
        register(BlockEntityType.BLAST_FURNACE, CraftBlastFurnace.class, CraftBlastFurnace::new);
        register(BlockEntityType.BREWING_STAND, CraftBrewingStand.class, CraftBrewingStand::new);
        register(BlockEntityType.COMPARATOR, CraftComparator.class, CraftComparator::new);
        register(BlockEntityType.CONDUIT, CraftConduit.class, CraftConduit::new);
        register(BlockEntityType.DAYLIGHT_DETECTOR, CraftDaylightDetector.class, CraftDaylightDetector::new);
        register(BlockEntityType.DISPENSER, CraftDispenser.class, CraftDispenser::new);
        register(BlockEntityType.DROPPER, CraftDropper.class, CraftDropper::new);
        register(BlockEntityType.ENCHANTING_TABLE, CraftEnchantingTable.class, CraftEnchantingTable::new);
        register(BlockEntityType.ENDER_CHEST, CraftEnderChest.class, CraftEnderChest::new);
        register(BlockEntityType.END_GATEWAY, CraftEndGateway.class, CraftEndGateway::new);
        register(BlockEntityType.END_PORTAL, CraftEndPortal.class, CraftEndPortal::new);
        register(BlockEntityType.FURNACE, CraftFurnaceFurnace.class, CraftFurnaceFurnace::new);
        register(BlockEntityType.HOPPER, CraftHopper.class, CraftHopper::new);
        register(BlockEntityType.JIGSAW, CraftJigsaw.class, CraftJigsaw::new);
        register(BlockEntityType.JUKEBOX, CraftJukebox.class, CraftJukebox::new);
        register(BlockEntityType.LECTERN, CraftLectern.class, CraftLectern::new);
        register(BlockEntityType.PISTON, CraftMovingPiston.class, CraftMovingPiston::new);
        register(BlockEntityType.SCULK_SENSOR, CraftSculkSensor.class, CraftSculkSensor::new);
        register(BlockEntityType.SMOKER, CraftSmoker.class, CraftSmoker::new);
        register(BlockEntityType.MOB_SPAWNER, CraftCreatureSpawner.class, CraftCreatureSpawner::new);
        register(BlockEntityType.STRUCTURE_BLOCK, CraftStructureBlock.class, CraftStructureBlock::new);
        // Paper end
    }

    private static void register(Material blockType, BlockStateFactory<?> factory) {
        CraftBlockStates.FACTORIES.put(blockType, factory);
    }

    private static <T extends BlockEntity, B extends CraftBlockEntityState<T>> void register(
            net.minecraft.world.level.block.entity.BlockEntityType<? extends T> blockEntityType, // Paper
            Class<B> blockStateType,
            BiFunction<World, T, B> blockStateConstructor // Paper
    ) {
        // Paper start
        BlockStateFactory<B> factory = new BlockEntityStateFactory<>(blockStateType, blockStateConstructor, blockEntityType::create);
        for (net.minecraft.world.level.block.Block block : blockEntityType.validBlocks) {
            CraftBlockStates.register(CraftMagicNumbers.getMaterial(block), factory);
        }
        CraftBlockStates.register(blockEntityType, factory);
        // Paper end
    }

    private static BlockStateFactory<?> getFactory(Material material) {
        return CraftBlockStates.FACTORIES.getOrDefault(material, DEFAULT_FACTORY);
    }

    // Paper start
    private static BlockStateFactory<?> getFactory(Material material, BlockEntityType<?> type) {
        if (type != null) {
            return CraftBlockStates.FACTORIES_BY_BLOCK_ENTITY_TYPE.getOrDefault(type, getFactory(material));
        } else {
            return getFactory(material);
        }
    }
    // Paper end

    public static Class<? extends CraftBlockState> getBlockStateType(Material material) {
        Preconditions.checkNotNull(material, "material is null");
        return CraftBlockStates.getFactory(material).blockStateType;
    }

    // Paper start
    public static Class<? extends CraftBlockState> getBlockStateType(BlockEntityType<?> blockEntityType) {
        Preconditions.checkNotNull(blockEntityType, "blockEntityType is null");
        return CraftBlockStates.getFactory(null, blockEntityType).blockStateType;
    }
    // Paper end

    public static BlockState getBlockState(Block block) {
        Preconditions.checkNotNull(block, "block is null");
        CraftBlock craftBlock = (CraftBlock) block;
        CraftWorld world = (CraftWorld) block.getWorld();
        BlockPos blockPosition = craftBlock.getPosition();
        net.minecraft.world.level.block.state.BlockState blockData = craftBlock.getNMS();
        BlockEntity tileEntity = craftBlock.getHandle().getBlockEntity(blockPosition);
        CraftBlockState blockState = CraftBlockStates.getBlockState(world, blockPosition, blockData, tileEntity);
        blockState.setWorldHandle(craftBlock.getHandle()); // Inject the block's generator access
        return blockState;
    }

    public static BlockState getBlockState(Material material, @Nullable CompoundTag blockEntityTag) {
        return CraftBlockStates.getBlockState(BlockPos.ZERO, material, blockEntityTag);
    }

    public static BlockState getBlockState(BlockPos blockPosition, Material material, @Nullable CompoundTag blockEntityTag) {
        Preconditions.checkNotNull(material, "material is null");
        net.minecraft.world.level.block.state.BlockState blockData = CraftMagicNumbers.getBlock(material).defaultBlockState();
        return CraftBlockStates.getBlockState(blockPosition, blockData, blockEntityTag);
    }

    public static BlockState getBlockState(net.minecraft.world.level.block.state.BlockState blockData, @Nullable CompoundTag blockEntityTag) {
        return CraftBlockStates.getBlockState(BlockPos.ZERO, blockData, blockEntityTag);
    }

    public static BlockState getBlockState(BlockPos blockPosition, net.minecraft.world.level.block.state.BlockState blockData, @Nullable CompoundTag blockEntityTag) {
        Preconditions.checkNotNull(blockPosition, "blockPosition is null");
        Preconditions.checkNotNull(blockData, "blockData is null");
        BlockEntity tileEntity = (blockEntityTag == null) ? null : BlockEntity.loadStatic(blockPosition, blockData, blockEntityTag);
        return CraftBlockStates.getBlockState(null, blockPosition, blockData, tileEntity);
    }

    // See BlockStateFactory#createBlockState(World, BlockPosition, IBlockData, TileEntity)
    private static CraftBlockState getBlockState(World world, BlockPos blockPosition, net.minecraft.world.level.block.state.BlockState blockData, BlockEntity tileEntity) {
        Material material = CraftMagicNumbers.getMaterial(blockData.getBlock());
        BlockStateFactory<?> factory;
        // For some types of TileEntity blocks (eg. moving pistons), Minecraft may in some situations (eg. when using Block#setType or the
        // setBlock command) not create a corresponding TileEntity in the world. We return a normal BlockState in this case.
        if (world != null && tileEntity == null && CraftBlockStates.isTileEntityOptional(material)) {
            factory = CraftBlockStates.DEFAULT_FACTORY;
        } else {
            factory = CraftBlockStates.getFactory(material, tileEntity != null ? tileEntity.getType() : null); // Paper
        }
        return factory.createBlockState(world, blockPosition, blockData, tileEntity);
    }

    private static boolean isTileEntityOptional(Material material) {
        return material == Material.MOVING_PISTON;
    }

    // This ignores tile entity data.
    public static CraftBlockState getBlockState(LevelAccessor world, BlockPos pos) {
        return new CraftBlockState(CraftBlock.at(world, pos));
    }

    // This ignores tile entity data.
    public static CraftBlockState getBlockState(LevelAccessor world, BlockPos pos, int flag) {
        return new CraftBlockState(CraftBlock.at(world, pos), flag);
    }

    private CraftBlockStates() {
    }
}
