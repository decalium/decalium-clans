package org.bukkit.craftbukkit.v1_17_R1.block;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftFluidCollisionMode;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftRayTraceResult;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVoxelShape;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class CraftBlock implements Block {
    private final net.minecraft.world.level.LevelAccessor world;
    private final BlockPos position;

    public CraftBlock(LevelAccessor world, BlockPos position) {
        this.world = world;
        this.position = position.immutable();
    }

    public static CraftBlock at(LevelAccessor world, BlockPos position) {
        return new CraftBlock(world, position);
    }

    public net.minecraft.world.level.block.state.BlockState getNMS() {
        return this.world.getBlockState(position);
    }

    public BlockPos getPosition() {
        return this.position;
    }

    @Override
    public World getWorld() {
        return this.world.getMinecraftWorld().getWorld();
    }

    public CraftWorld getCraftWorld() {
        return (CraftWorld) this.getWorld();
    }

    @Override
    public Location getLocation() {
        return new Location(this.getWorld(), this.position.getX(), this.position.getY(), this.position.getZ());
    }

    @Override
    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(this.getWorld());
            loc.setX(this.position.getX());
            loc.setY(this.position.getY());
            loc.setZ(this.position.getZ());
            loc.setYaw(0);
            loc.setPitch(0);
        }

        return loc;
    }

    public BlockVector getVector() {
        return new BlockVector(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public int getX() {
        return this.position.getX();
    }

    @Override
    public int getY() {
        return this.position.getY();
    }

    @Override
    public int getZ() {
        return this.position.getZ();
    }

    @Override
    public Chunk getChunk() {
        return this.getWorld().getChunkAt(this);
    }

    public void setData(final byte data) {
        this.setData(data, 3);
    }

    public void setData(final byte data, boolean applyPhysics) {
        if (applyPhysics) {
            this.setData(data, 3);
        } else {
            this.setData(data, 2);
        }
    }

    private void setData(final byte data, int flag) {
        this.world.setBlock(position, CraftMagicNumbers.getBlock(this.getType(), data), flag);
    }

    @Override
    public byte getData() {
        net.minecraft.world.level.block.state.BlockState blockData = this.world.getBlockState(position);
        return CraftMagicNumbers.toLegacyData(blockData);
    }

    @Override
    public BlockData getBlockData() {
        return CraftBlockData.fromData(this.getNMS());
    }

    @Override
    public void setType(final Material type) {
        this.setType(type, true);
    }

    @Override
    public void setType(Material type, boolean applyPhysics) {
        Preconditions.checkArgument(type != null, "Material cannot be null");
        this.setBlockData(type.createBlockData(), applyPhysics);
    }

    @Override
    public void setBlockData(BlockData data) {
        this.setBlockData(data, true);
    }

    @Override
    public void setBlockData(BlockData data, boolean applyPhysics) {
        Preconditions.checkArgument(data != null, "BlockData cannot be null");
        this.setTypeAndData(((CraftBlockData) data).getState(), applyPhysics);
    }

    public boolean setTypeAndData(final net.minecraft.world.level.block.state.BlockState blockData, final boolean applyPhysics) {
        net.minecraft.world.level.block.state.BlockState old = this.getNMS();
        // SPIGOT-611: need to do this to prevent glitchiness. Easier to handle this here (like /setblock) than to fix weirdness in tile entity cleanup
        if (old.hasBlockEntity() && blockData.getBlock() != old.getBlock()) { // SPIGOT-3725 remove old tile entity if block changes
            // SPIGOT-4612: faster - just clear tile
            if (this.world instanceof net.minecraft.world.level.Level) {
                ((net.minecraft.world.level.Level) this.world).removeBlockEntity(position);
            } else {
                this.world.setBlock(position, Blocks.AIR.defaultBlockState(), 0);
            }
        }

        if (applyPhysics) {
            return this.world.setBlock(position, blockData, 3);
        } else {
            boolean success = this.world.setBlock(position, blockData, 2 | 16 | 1024); // NOTIFY | NO_OBSERVER | NO_PLACE (custom)
            if (success) {
                this.world.getMinecraftWorld().sendBlockUpdated(
                        position,
                        old,
                        blockData,
                        3
                );
            }
            return success;
        }
    }

    @Override
    public Material getType() {
        return this.world.getBlockState(this.position).getBukkitMaterial(); // Paper - optimise getType calls
    }

    @Override
    public byte getLightLevel() {
        return (byte) this.world.getMinecraftWorld().getMaxLocalRawBrightness(position);
    }

    @Override
    public byte getLightFromSky() {
        return (byte) this.world.getBrightness(LightLayer.SKY, position);
    }

    @Override
    public byte getLightFromBlocks() {
        return (byte) this.world.getBrightness(LightLayer.BLOCK, position);
    }

    public Block getFace(final BlockFace face) {
        return this.getRelative(face, 1);
    }

    public Block getFace(final BlockFace face, final int distance) {
        return this.getRelative(face, distance);
    }

    @Override
    public Block getRelative(final int modX, final int modY, final int modZ) {
        return this.getWorld().getBlockAt(this.getX() + modX, this.getY() + modY, this.getZ() + modZ);
    }

    @Override
    public Block getRelative(BlockFace face) {
        return this.getRelative(face, 1);
    }

    @Override
    public Block getRelative(BlockFace face, int distance) {
        return this.getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    @Override
    public BlockFace getFace(final Block block) {
        BlockFace[] values = BlockFace.values();

        for (BlockFace face : values) {
            if ((this.getX() + face.getModX() == block.getX()) && (this.getY() + face.getModY() == block.getY()) && (this.getZ() + face.getModZ() == block.getZ())) {
                return face;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "CraftBlock{pos=" + this.position + ",type=" + this.getType() + ",data=" + this.getNMS() + ",fluid=" + this.world.getFluidState(position) + '}';
    }

    public static BlockFace notchToBlockFace(Direction notch) {
        if (notch == null) {
            return BlockFace.SELF;
        }
        switch (notch) {
            case DOWN:
                return BlockFace.DOWN;
            case UP:
                return BlockFace.UP;
            case NORTH:
                return BlockFace.NORTH;
            case SOUTH:
                return BlockFace.SOUTH;
            case WEST:
                return BlockFace.WEST;
            case EAST:
                return BlockFace.EAST;
            default:
                return BlockFace.SELF;
        }
    }

    public static Direction blockFaceToNotch(BlockFace face) {
        switch (face) {
            case DOWN:
                return Direction.DOWN;
            case UP:
                return Direction.UP;
            case NORTH:
                return Direction.NORTH;
            case SOUTH:
                return Direction.SOUTH;
            case WEST:
                return Direction.WEST;
            case EAST:
                return Direction.EAST;
            default:
                return null;
        }
    }

    @Override
    public BlockState getState() {
        // Paper start - allow disabling the use of snapshots
        return getState(true);
    }
    public BlockState getState(boolean useSnapshot) {
        boolean prev = CraftBlockEntityState.DISABLE_SNAPSHOT;
        CraftBlockEntityState.DISABLE_SNAPSHOT = !useSnapshot;
        try {
            return getState0();
        } finally {
            CraftBlockEntityState.DISABLE_SNAPSHOT = prev;
        }
    }
    public BlockState getState0() {
        // Paper end
        Material material = getType();

        switch (material) {
            case ACACIA_SIGN:
            case ACACIA_WALL_SIGN:
            case BIRCH_SIGN:
            case BIRCH_WALL_SIGN:
            case CRIMSON_SIGN:
            case CRIMSON_WALL_SIGN:
            case DARK_OAK_SIGN:
            case DARK_OAK_WALL_SIGN:
            case JUNGLE_SIGN:
            case JUNGLE_WALL_SIGN:
            case OAK_SIGN:
            case OAK_WALL_SIGN:
            case SPRUCE_SIGN:
            case SPRUCE_WALL_SIGN:
            case WARPED_SIGN:
            case WARPED_WALL_SIGN:
                return new CraftSign(this);
            case CHEST:
            case TRAPPED_CHEST:
                return new CraftChest(this);
            case FURNACE:
                return new CraftFurnaceFurnace(this);
            case DISPENSER:
                return new CraftDispenser(this);
            case DROPPER:
                return new CraftDropper(this);
            case END_GATEWAY:
                return new CraftEndGateway(this);
            case HOPPER:
                return new CraftHopper(this);
            case SPAWNER:
                return new CraftCreatureSpawner(this);
            case JUKEBOX:
                return new CraftJukebox(this);
            case BREWING_STAND:
                return new CraftBrewingStand(this);
            case CREEPER_HEAD:
            case CREEPER_WALL_HEAD:
            case DRAGON_HEAD:
            case DRAGON_WALL_HEAD:
            case PLAYER_HEAD:
            case PLAYER_WALL_HEAD:
            case SKELETON_SKULL:
            case SKELETON_WALL_SKULL:
            case WITHER_SKELETON_SKULL:
            case WITHER_SKELETON_WALL_SKULL:
            case ZOMBIE_HEAD:
            case ZOMBIE_WALL_HEAD:
                return new CraftSkull(this);
            case COMMAND_BLOCK:
            case CHAIN_COMMAND_BLOCK:
            case REPEATING_COMMAND_BLOCK:
                return new CraftCommandBlock(this);
            case BEACON:
                return new CraftBeacon(this);
            case BLACK_BANNER:
            case BLACK_WALL_BANNER:
            case BLUE_BANNER:
            case BLUE_WALL_BANNER:
            case BROWN_BANNER:
            case BROWN_WALL_BANNER:
            case CYAN_BANNER:
            case CYAN_WALL_BANNER:
            case GRAY_BANNER:
            case GRAY_WALL_BANNER:
            case GREEN_BANNER:
            case GREEN_WALL_BANNER:
            case LIGHT_BLUE_BANNER:
            case LIGHT_BLUE_WALL_BANNER:
            case LIGHT_GRAY_BANNER:
            case LIGHT_GRAY_WALL_BANNER:
            case LIME_BANNER:
            case LIME_WALL_BANNER:
            case MAGENTA_BANNER:
            case MAGENTA_WALL_BANNER:
            case ORANGE_BANNER:
            case ORANGE_WALL_BANNER:
            case PINK_BANNER:
            case PINK_WALL_BANNER:
            case PURPLE_BANNER:
            case PURPLE_WALL_BANNER:
            case RED_BANNER:
            case RED_WALL_BANNER:
            case WHITE_BANNER:
            case WHITE_WALL_BANNER:
            case YELLOW_BANNER:
            case YELLOW_WALL_BANNER:
                return new CraftBanner(this);
            case STRUCTURE_BLOCK:
                return new CraftStructureBlock(this);
            case SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case BLACK_SHULKER_BOX:
                return new CraftShulkerBox(this);
            case ENCHANTING_TABLE:
                return new CraftEnchantingTable(this);
            case ENDER_CHEST:
                return new CraftEnderChest(this);
            case DAYLIGHT_DETECTOR:
                return new CraftDaylightDetector(this);
            case COMPARATOR:
                return new CraftComparator(this);
            case BLACK_BED:
            case BLUE_BED:
            case BROWN_BED:
            case CYAN_BED:
            case GRAY_BED:
            case GREEN_BED:
            case LIGHT_BLUE_BED:
            case LIGHT_GRAY_BED:
            case LIME_BED:
            case MAGENTA_BED:
            case ORANGE_BED:
            case PINK_BED:
            case PURPLE_BED:
            case RED_BED:
            case WHITE_BED:
            case YELLOW_BED:
                return new CraftBed(this);
            case CONDUIT:
                return new CraftConduit(this);
            case BARREL:
                return new CraftBarrel(this);
            case BELL:
                return new CraftBell(this);
            case BLAST_FURNACE:
                return new CraftBlastFurnace(this);
            case CAMPFIRE:
            case SOUL_CAMPFIRE:
                return new CraftCampfire(this);
            case JIGSAW:
                return new CraftJigsaw(this);
            case LECTERN:
                return new CraftLectern(this);
            case SMOKER:
                return new CraftSmoker(this);
            case BEEHIVE:
            case BEE_NEST:
                return new CraftBeehive(this);
            case SCULK_SENSOR:
                return new CraftSculkSensor(this);
            default:
                BlockEntity tileEntity = this.world.getBlockEntity(position);
                if (tileEntity != null) {
                    // block with unhandled TileEntity:
                    return new CraftBlockEntityState<BlockEntity>(this, (Class<BlockEntity>) tileEntity.getClass());
                } else {
                    // Block without TileEntity:
                    return new CraftBlockState(this);
                }
        }
    }

    @Override
    public Biome getBiome() {
        return this.getWorld().getBiome(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public void setBiome(Biome bio) {
        this.getWorld().setBiome(this.getX(), this.getY(), this.getZ(), bio);
    }

    public static Biome biomeBaseToBiome(net.minecraft.core.Registry<net.minecraft.world.level.biome.Biome> registry, net.minecraft.world.level.biome.Biome base) {
        if (base == null) {
            return null;
        }

        Biome biome = Registry.BIOME.get(CraftNamespacedKey.fromMinecraft(registry.getKey(base)));
        return (biome == null) ? Biome.CUSTOM : biome;
    }

    public static net.minecraft.world.level.biome.Biome biomeToBiomeBase(net.minecraft.core.Registry<net.minecraft.world.level.biome.Biome> registry, Biome bio) {
        if (bio == null || bio == Biome.CUSTOM) {
            return null;
        }

        return registry.get(CraftNamespacedKey.toMinecraft(bio.getKey()));
    }

    @Override
    public double getTemperature() {
        return this.world.getBiome(position).getTemperature(position);
    }

    @Override
    public double getHumidity() {
        return this.getWorld().getHumidity(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public boolean isBlockPowered() {
        return this.world.getMinecraftWorld().getDirectSignalTo(position) > 0;
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return this.world.getMinecraftWorld().hasNeighborSignal(position);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CraftBlock)) {
            return false;
        }
        CraftBlock other = (CraftBlock) o;

        return this.position.equals(other.position) && this.getWorld().equals(other.getWorld());
    }

    @Override
    public int hashCode() {
        return this.position.hashCode() ^ this.getWorld().hashCode();
    }

    @Override
    public boolean isBlockFacePowered(BlockFace face) {
        return this.world.getMinecraftWorld().hasSignal(position, CraftBlock.blockFaceToNotch(face));
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
        int power = this.world.getMinecraftWorld().getSignal(position, CraftBlock.blockFaceToNotch(face));

        Block relative = this.getRelative(face);
        if (relative.getType() == Material.REDSTONE_WIRE) {
            return Math.max(power, relative.getData()) > 0;
        }

        return power > 0;
    }

    @Override
    public int getBlockPower(BlockFace face) {
        int power = 0;
        net.minecraft.world.level.Level world = this.world.getMinecraftWorld();
        int x = this.getX();
        int y = this.getY();
        int z = this.getZ();
        if ((face == BlockFace.DOWN || face == BlockFace.SELF) && world.hasSignal(new BlockPos(x, y - 1, z), Direction.DOWN)) power = CraftBlock.getPower(power, world.getBlockState(new BlockPos(x, y - 1, z)));
        if ((face == BlockFace.UP || face == BlockFace.SELF) && world.hasSignal(new BlockPos(x, y + 1, z), Direction.UP)) power = CraftBlock.getPower(power, world.getBlockState(new BlockPos(x, y + 1, z)));
        if ((face == BlockFace.EAST || face == BlockFace.SELF) && world.hasSignal(new BlockPos(x + 1, y, z), Direction.EAST)) power = CraftBlock.getPower(power, world.getBlockState(new BlockPos(x + 1, y, z)));
        if ((face == BlockFace.WEST || face == BlockFace.SELF) && world.hasSignal(new BlockPos(x - 1, y, z), Direction.WEST)) power = CraftBlock.getPower(power, world.getBlockState(new BlockPos(x - 1, y, z)));
        if ((face == BlockFace.NORTH || face == BlockFace.SELF) && world.hasSignal(new BlockPos(x, y, z - 1), Direction.NORTH)) power = CraftBlock.getPower(power, world.getBlockState(new BlockPos(x, y, z - 1)));
        if ((face == BlockFace.SOUTH || face == BlockFace.SELF) && world.hasSignal(new BlockPos(x, y, z + 1), Direction.SOUTH)) power = CraftBlock.getPower(power, world.getBlockState(new BlockPos(x, y, z + 1)));
        return power > 0 ? power : (face == BlockFace.SELF ? this.isBlockIndirectlyPowered() : this.isBlockFaceIndirectlyPowered(face)) ? 15 : 0;
    }

    private static int getPower(int i, net.minecraft.world.level.block.state.BlockState iblockdata) {
        if (!iblockdata.is(Blocks.REDSTONE_WIRE)) {
            return i;
        } else {
            int j = iblockdata.getValue(RedStoneWireBlock.POWER);

            return j > i ? j : i;
        }
    }

    @Override
    public int getBlockPower() {
        return this.getBlockPower(BlockFace.SELF);
    }

    @Override
    public boolean isEmpty() {
        return this.getNMS().isAir();
    }

    @Override
    public boolean isLiquid() {
        return this.getNMS().getMaterial().isLiquid();
    }

    // Paper start
    @Override
    public boolean isBuildable() {
        return getNMS().getMaterial().isSolid(); // This is in fact isSolid, despite the fact that isSolid below returns blocksMotion
    }
    @Override
    public boolean isBurnable() {
        return getNMS().getMaterial().isFlammable();
    }
    @Override
    public boolean isReplaceable() {
        return getNMS().getMaterial().isReplaceable();
    }
    @Override
    public boolean isSolid() {
        return getNMS().getMaterial().blocksMotion();
    }
    // Paper end

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return PistonMoveReaction.getById(this.getNMS().getPistonPushReaction().ordinal());
    }

    @Override
    public boolean breakNaturally() {
        return this.breakNaturally(null);
    }

    @Override
    public boolean breakNaturally(ItemStack item) {
        // Paper start
        return breakNaturally(item, false);
    }

    @Override
    public boolean breakNaturally(ItemStack item, boolean triggerEffect) {
        // Paper end
        // Order matters here, need to drop before setting to air so skulls can get their data
        net.minecraft.world.level.block.state.BlockState iblockdata = this.getNMS();
        net.minecraft.world.level.block.Block block = iblockdata.getBlock();
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        boolean result = false;

        // Modelled off EntityHuman#hasBlock
        if (block != Blocks.AIR && (item == null || !iblockdata.requiresCorrectToolForDrops() || nmsItem.isCorrectToolForDrops(iblockdata))) {
            net.minecraft.world.level.block.Block.dropResources(iblockdata, this.world.getMinecraftWorld(), position, this.world.getBlockEntity(position), null, nmsItem);
            if (triggerEffect) world.levelEvent(org.bukkit.Effect.STEP_SOUND.getId(), position, net.minecraft.world.level.block.Block.getId(block.defaultBlockState())); // Paper
            result = true;
        }

        return this.setTypeAndData(Blocks.AIR.defaultBlockState(), true) && result;
    }

    @Override
    public boolean applyBoneMeal(BlockFace face) {
        Direction direction = CraftBlock.blockFaceToNotch(face);
        UseOnContext context = new UseOnContext(this.getCraftWorld().getHandle(), null, InteractionHand.MAIN_HAND, Items.BONE_MEAL.getDefaultInstance(), new BlockHitResult(Vec3.ZERO, direction, this.getPosition(), false));

        return BoneMealItem.applyBonemeal(context) == InteractionResult.CONSUME; // Paper - CONSUME is returned on success server-side (see BoneMealItem.applyBoneMeal and InteractionResult.sidedSuccess(boolean))
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return this.getDrops(null);
    }

    @Override
    public Collection<ItemStack> getDrops(ItemStack item) {
        return this.getDrops(item, null);
    }

    @Override
    public Collection<ItemStack> getDrops(ItemStack item, Entity entity) {
        net.minecraft.world.level.block.state.BlockState iblockdata = this.getNMS();
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);

        // Modelled off EntityHuman#hasBlock
        if (item == null || this.isPreferredTool(iblockdata, nms)) {
            return net.minecraft.world.level.block.Block.getDrops(iblockdata, (ServerLevel) this.world.getMinecraftWorld(), position, this.world.getBlockEntity(position), entity == null ? null : ((CraftEntity) entity).getHandle(), nms)
                    .stream().map(CraftItemStack::asBukkitCopy).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isPreferredTool(ItemStack item) {
        net.minecraft.world.level.block.state.BlockState iblockdata = this.getNMS();
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        return this.isPreferredTool(iblockdata, nms);
    }

    @Override
    public float getBreakSpeed(Player player) {
        Preconditions.checkArgument(player != null, "player cannot be null");
        return this.getNMS().getDestroyProgress(((CraftPlayer) player).getHandle(), world, position);
    }

    private boolean isPreferredTool(net.minecraft.world.level.block.state.BlockState iblockdata, net.minecraft.world.item.ItemStack nmsItem) {
        return !iblockdata.requiresCorrectToolForDrops() || nmsItem.isCorrectToolForDrops(iblockdata);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.getCraftWorld().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.getCraftWorld().getBlockMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.getCraftWorld().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.getCraftWorld().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public boolean isPassable() {
        return this.getNMS().getCollisionShape(world, position).isEmpty();
    }

    @Override
    public RayTraceResult rayTrace(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode) {
        Validate.notNull(start, "Start location is null!");
        Validate.isTrue(this.getWorld().equals(start.getWorld()), "Start location is from different world!");
        start.checkFinite();

        Validate.notNull(direction, "Direction is null!");
        direction.checkFinite();
        Validate.isTrue(direction.lengthSquared() > 0, "Direction's magnitude is 0!");

        Validate.notNull(fluidCollisionMode, "Fluid collision mode is null!");
        if (maxDistance < 0.0D) {
            return null;
        }

        Vector dir = direction.clone().normalize().multiply(maxDistance);
        Vec3 startPos = new Vec3(start.getX(), start.getY(), start.getZ());
        Vec3 endPos = new Vec3(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());

        HitResult nmsHitResult = this.world.rayTraceBlock(new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, CraftFluidCollisionMode.toNMS(fluidCollisionMode), null), position);
        return CraftRayTraceResult.fromNMS(this.getWorld(), nmsHitResult);
    }

    @Override
    public BoundingBox getBoundingBox() {
        VoxelShape shape = this.getNMS().getShape(world, position);

        if (shape.isEmpty()) {
            return new BoundingBox(); // Return an empty bounding box if the block has no dimension
        }

        AABB aabb = shape.bounds();
        return new BoundingBox(this.getX() + aabb.minX, this.getY() + aabb.minY, this.getZ() + aabb.minZ, this.getX() + aabb.maxX, this.getY() + aabb.maxY, this.getZ() + aabb.maxZ);
    }

    @Override
    public org.bukkit.util.VoxelShape getCollisionShape() {
        VoxelShape shape = this.getNMS().getCollisionShape(world, position);
        return new CraftVoxelShape(shape);
    }
    // Paper start
    @Override
    public com.destroystokyo.paper.block.BlockSoundGroup getSoundGroup() {
        return new com.destroystokyo.paper.block.CraftBlockSoundGroup(getNMS().getBlock().defaultBlockState().getSoundType());
    }

    @Override
    public String getTranslationKey() {
        return org.bukkit.Bukkit.getUnsafe().getTranslationKey(this);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, boolean considerEnchants) {
        net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof CraftItemStack) {
            nmsItemStack = ((CraftItemStack) itemStack).handle;
        } else {
            nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        }
        float speed = nmsItemStack.getItem().getDestroySpeed(nmsItemStack, this.getNMS().getBlock().defaultBlockState());
        if (speed > 1.0F && considerEnchants) {
            int enchantLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_EFFICIENCY, nmsItemStack);
            if (enchantLevel > 0) {
                speed += enchantLevel * enchantLevel + 1;
            }
        }
        return speed;
    }

    public boolean isValidTool(ItemStack itemStack) {
        return getDrops(itemStack).size() != 0;
    }
    // Paper end
}
