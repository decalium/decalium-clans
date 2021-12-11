package org.bukkit.craftbukkit.v1_18_R1;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang.Validate;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.StructureType;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_18_R1.boss.CraftDragonBattle;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R1.metadata.BlockMetadataStore;
import org.bukkit.craftbukkit.v1_18_R1.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftRayTraceResult;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class CraftWorld extends CraftRegionAccessor implements World {
    public static final int CUSTOM_DIMENSION_OFFSET = 10;

    private final ServerLevel world;
    private WorldBorder worldBorder;
    private Environment environment;
    private final CraftServer server = (CraftServer) Bukkit.getServer();
    private final ChunkGenerator generator;
    private final BiomeProvider biomeProvider;
    private final List<BlockPopulator> populators = new ArrayList<BlockPopulator>();
    private final BlockMetadataStore blockMetadata = new BlockMetadataStore(this);
    private int monsterSpawn = -1;
    private int animalSpawn = -1;
    private int waterAnimalSpawn = -1;
    private int waterAmbientSpawn = -1;
    private int waterUndergroundCreatureSpawn = -1;
    private int ambientSpawn = -1;
    private net.kyori.adventure.pointer.Pointers adventure$pointers; // Paper - implement pointers

    // Paper start - Provide fast information methods
    @Override
    public int getEntityCount() {
        int ret = 0;
        for (net.minecraft.world.entity.Entity entity : world.getEntities().getAll()) {
            if (entity.isChunkLoaded()) {
                ++ret;
            }
        }
        return ret;
    }

    @Override
    public int getTileEntityCount() {
        // We don't use the full world tile entity list, so we must iterate chunks
        Long2ObjectLinkedOpenHashMap<ChunkHolder> chunks = world.getChunkSource().chunkMap.updatingChunks.getVisibleMap(); // Paper - change updating chunks map
        int size = 0;
        for (ChunkHolder playerchunk : chunks.values()) {
            net.minecraft.world.level.chunk.LevelChunk chunk = playerchunk.getTickingChunk();
            if (chunk == null) {
                continue;
            }
            size += chunk.blockEntities.size();
        }
        return size;
    }

    @Override
    public int getTickableTileEntityCount() {
        return world.getTotalTileEntityTickers();
    }

    @Override
    public int getChunkCount() {
        int ret = 0;

        for (ChunkHolder chunkHolder : world.getChunkSource().chunkMap.updatingChunks.getVisibleMap().values()) { // Paper - change updating chunks map
            if (chunkHolder.getTickingChunk() != null) {
                ++ret;
            }
        }

        return ret;
    }

    @Override
    public int getPlayerCount() {
        return world.players().size();
    }

    @Override
    public io.papermc.paper.world.MoonPhase getMoonPhase() {
        return io.papermc.paper.world.MoonPhase.getPhase(getFullTime() / 24000L);
    }

    @Override
    public boolean lineOfSightExists(Location from, Location to) {
        Validate.notNull(from, "from parameter in lineOfSightExists cannot be null");
        Validate.notNull(to, "to parameter in lineOfSightExists cannot be null");
        if (from.getWorld() != to.getWorld()) return false;
        Vec3 vec3d = new Vec3(from.getX(), from.getY(), from.getZ());
        Vec3 vec3d1 = new Vec3(to.getX(), to.getY(), to.getZ());
        if (vec3d1.distanceToSqr(vec3d) > 128D * 128D) return false; //Return early if the distance is greater than 128 blocks

        return this.getHandle().clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getType() == HitResult.Type.MISS;
    }
    // Paper end

    private static final Random rand = new Random();

    public CraftWorld(ServerLevel world, ChunkGenerator gen, BiomeProvider biomeProvider, Environment env) {
        this.world = world;
        this.generator = gen;
        this.biomeProvider = biomeProvider;

        this.environment = env;
        // Paper start - per world spawn limits
        this.monsterSpawn = this.world.paperConfig.perWorldSpawnLimits.getInt(net.minecraft.world.entity.MobCategory.MONSTER);
        this.animalSpawn = this.world.paperConfig.perWorldSpawnLimits.getInt(net.minecraft.world.entity.MobCategory.CREATURE);
        this.waterAnimalSpawn = this.world.paperConfig.perWorldSpawnLimits.getInt(net.minecraft.world.entity.MobCategory.WATER_CREATURE);
        this.waterAmbientSpawn = this.world.paperConfig.perWorldSpawnLimits.getInt(net.minecraft.world.entity.MobCategory.WATER_AMBIENT);
        this.ambientSpawn = this.world.paperConfig.perWorldSpawnLimits.getInt(net.minecraft.world.entity.MobCategory.AMBIENT);
        this.waterUndergroundCreatureSpawn = this.world.paperConfig.perWorldSpawnLimits.getInt(net.minecraft.world.entity.MobCategory.UNDERGROUND_WATER_CREATURE);
        // Paper end
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        return CraftBlock.at(world, new BlockPos(x, y, z));
    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        return this.getHighestBlockYAt(x, z, org.bukkit.HeightMap.MOTION_BLOCKING);
    }

    // Paper start - Implement heightmap api
    @Override
    public int getHighestBlockYAt(final int x, final int z, final com.destroystokyo.paper.HeightmapType heightmap) throws UnsupportedOperationException {
        this.getChunkAt(x >> 4, z >> 4); // heightmap will ret 0 on unloaded areas

        switch (heightmap) {
            case LIGHT_BLOCKING:
                throw new UnsupportedOperationException(); // TODO
                //return this.world.getHighestBlockY(HeightMap.Type.LIGHT_BLOCKING, x, z);
            case ANY:
                return this.world.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, x, z);
            case SOLID:
                return this.world.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.OCEAN_FLOOR, x, z);
            case SOLID_OR_LIQUID:
                return this.world.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, x, z);
            case SOLID_OR_LIQUID_NO_LEAVES:
                return this.world.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            default:
                throw new UnsupportedOperationException();
        }
    }
    // Paper end

    @Override
    public Location getSpawnLocation() {
        BlockPos spawn = this.world.getSharedSpawnPos();
        return new Location(this, spawn.getX(), spawn.getY(), spawn.getZ(), world.levelData.getSpawnAngle(), 0.0F); // Paper - expose world spawn angle
    }

    @Override
    public boolean setSpawnLocation(Location location) {
        Preconditions.checkArgument(location != null, "location");

        return this.equals(location.getWorld()) ? this.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw()) : false;
    }

    @Override
    public boolean setSpawnLocation(int x, int y, int z, float angle) {
        try {
            Location previousLocation = this.getSpawnLocation();
            world.setDefaultSpawnPos(new BlockPos(x, y, z), angle); // Paper - use WorldServer#setSpawn

            // Paper start - move to nms.World
            // Notify anyone who's listening.
            // SpawnChangeEvent event = new SpawnChangeEvent(this, previousLocation);
            // server.getPluginManager().callEvent(event);
            // Paper end

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setSpawnLocation(int x, int y, int z) {
        return this.setSpawnLocation(x, y, z, 0.0F);
    }

    @Override
    public Chunk getChunkAt(int x, int z) {
        // Paper start - add ticket to hold chunk for a little while longer if plugin accesses it
        net.minecraft.world.level.chunk.LevelChunk chunk = world.getChunkSource().getChunkAtIfLoadedImmediately(x, z);
        if (chunk == null) {
            addTicket(x, z);
            chunk = this.world.getChunkSource().getChunk(x, z, true);
        }
        return chunk.bukkitChunk;
        // Paper end
    }

    // Paper start
    private void addTicket(int x, int z) {
        net.minecraft.server.MCUtil.MAIN_EXECUTOR.execute(() -> world.getChunkSource().addRegionTicket(TicketType.PLUGIN, new ChunkPos(x, z), 0, Unit.INSTANCE)); // Paper
    }
    // Paper end

    @Override
    public Chunk getChunkAt(Block block) {
        Preconditions.checkArgument(block != null, "null block");

        return this.getChunkAt(block.getX() >> 4, block.getZ() >> 4);
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return this.world.getChunkSource().getChunkAtIfLoadedImmediately(x, z) != null; // Paper
    }

    @Override
    public boolean isChunkGenerated(int x, int z) {
        // Paper start - Fix this method
        if (!Bukkit.isPrimaryThread()) {
            return CompletableFuture.supplyAsync(() -> {
                return CraftWorld.this.isChunkGenerated(x, z);
            }, world.getChunkSource().mainThreadProcessor).join();
        }
        ChunkAccess chunk = world.getChunkSource().getChunkAtImmediately(x, z);
        if (chunk == null) {
            chunk = world.getChunkSource().chunkMap.getUnloadingChunk(x, z);
        }
        if (chunk != null) {
            return chunk instanceof ImposterProtoChunk || chunk instanceof net.minecraft.world.level.chunk.LevelChunk;
        }
        try {
            return world.getChunkSource().chunkMap.getChunkStatusOnDisk(new ChunkPos(x, z)) == ChunkStatus.FULL;
            // Paper end
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Chunk[] getLoadedChunks() {
        // Paper start
        if (Thread.currentThread() != world.getLevel().thread) {
            // Paper start - change updating chunks map
            Long2ObjectLinkedOpenHashMap<ChunkHolder> chunks;
            synchronized (world.getChunkSource().chunkMap.updatingChunks) {
                chunks = world.getChunkSource().chunkMap.updatingChunks.getVisibleMap().clone();
            }
            return chunks.values().stream().map(ChunkHolder::getFullChunk).filter(Objects::nonNull).map(net.minecraft.world.level.chunk.LevelChunk::getBukkitChunk).toArray(Chunk[]::new);
            // Paper end - change updating chunks map
        }
        // Paper end
        Long2ObjectLinkedOpenHashMap<ChunkHolder> chunks = world.getChunkSource().chunkMap.updatingChunks.getVisibleMap(); // Paper - change updating chunks map
        return chunks.values().stream().map(ChunkHolder::getFullChunk).filter(Objects::nonNull).map(net.minecraft.world.level.chunk.LevelChunk::getBukkitChunk).toArray(Chunk[]::new);
    }

    @Override
    public void loadChunk(int x, int z) {
        this.loadChunk(x, z, true);
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
        return this.unloadChunk(chunk.getX(), chunk.getZ());
    }

    @Override
    public boolean unloadChunk(int x, int z) {
        return this.unloadChunk(x, z, true);
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean save) {
        return this.unloadChunk0(x, z, save);
    }

    @Override
    public boolean unloadChunkRequest(int x, int z) {
        org.spigotmc.AsyncCatcher.catchOp("chunk unload"); // Spigot
        if (this.isChunkLoaded(x, z)) {
            this.world.getChunkSource().removeRegionTicket(TicketType.PLUGIN, new ChunkPos(x, z), 0, Unit.INSTANCE); // Paper
        }

        return true;
    }

    private boolean unloadChunk0(int x, int z, boolean save) {
        org.spigotmc.AsyncCatcher.catchOp("chunk unload"); // Spigot
        if (!this.isChunkLoaded(x, z)) {
            return true;
        }
        net.minecraft.world.level.chunk.LevelChunk chunk = this.world.getChunk(x, z);

        chunk.mustNotSave = !save;
        this.unloadChunkRequest(x, z);

        this.world.getChunkSource().purgeUnload();
        return !this.isChunkLoaded(x, z);
    }

    @Override
    public boolean regenerateChunk(int x, int z) {
        org.spigotmc.AsyncCatcher.catchOp("chunk regenerate"); // Spigot
        throw new UnsupportedOperationException("Not supported in this Minecraft version! Unless you can fix it, this is not a bug :)");
        /*
        if (!unloadChunk0(x, z, false)) {
            return false;
        }

        final long chunkKey = ChunkCoordIntPair.pair(x, z);
        world.getChunkProvider().unloadQueue.remove(chunkKey);

        net.minecraft.server.Chunk chunk = world.getChunkProvider().generateChunk(x, z);
        PlayerChunk playerChunk = world.getPlayerChunkMap().getChunk(x, z);
        if (playerChunk != null) {
            playerChunk.chunk = chunk;
        }

        if (chunk != null) {
            refreshChunk(x, z);
        }

        return chunk != null;
        */
    }

    @Override
    public boolean refreshChunk(int x, int z) {
        ChunkHolder playerChunk = this.world.getChunkSource().chunkMap.updatingChunks.getVisibleMap().get(ChunkPos.asLong(x, z));
        if (playerChunk == null) return false;

        playerChunk.getTickingChunkFuture().thenAccept(either -> {
            either.left().ifPresent(chunk -> {
                List<ServerPlayer> playersInRange = playerChunk.playerProvider.getPlayers(playerChunk.getPos(), false);
                if (playersInRange.isEmpty()) return;

                ClientboundLevelChunkWithLightPacket refreshPacket = new ClientboundLevelChunkWithLightPacket(chunk, this.world.getLightEngine(), null, null, true);
                for (ServerPlayer player : playersInRange) {
                    if (player.connection == null) continue;

                    player.connection.send(refreshPacket);
                }
            });
        });

        return true;
    }

    @Override
    public boolean isChunkInUse(int x, int z) {
        return this.isChunkLoaded(x, z);
    }

    @Override
    public boolean loadChunk(int x, int z, boolean generate) {
        org.spigotmc.AsyncCatcher.catchOp("chunk load"); // Spigot
        // Paper start - Optimize this method
        ChunkPos chunkPos = new ChunkPos(x, z);
        ChunkAccess immediate = world.getChunkSource().getChunkAtIfLoadedImmediately(x, z); // Paper
        if (immediate != null) return true; // Paper

        if (!generate) {

            //IChunkAccess immediate = world.getChunkProvider().getChunkAtImmediately(x, z); // Paper
            if (immediate == null) {
                immediate = world.getChunkSource().chunkMap.getUnloadingChunk(x, z);
            }
            if (immediate != null) {
                if (!(immediate instanceof ImposterProtoChunk) && !(immediate instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                    return false; // not full status
                }
                world.getChunkSource().addRegionTicket(TicketType.PLUGIN, chunkPos, 0, Unit.INSTANCE); // Paper
                world.getChunk(x, z); // make sure we're at ticket level 32 or lower
                return true;
            }

            net.minecraft.world.level.chunk.storage.RegionFile file;
            try {
                file = world.getChunkSource().chunkMap.regionFileCache.getRegionFile(chunkPos, false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            ChunkStatus status = file.getStatusIfCached(x, z);
            if (!file.hasChunk(chunkPos) || (status != null && status != ChunkStatus.FULL)) {
                return false;
            }

            ChunkAccess chunk = world.getChunkSource().getChunk(x, z, ChunkStatus.EMPTY, true);
            if (!(chunk instanceof ImposterProtoChunk) && !(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                return false;
            }

            // fall through to load
            // we do this so we do not re-read the chunk data on disk
        }

        world.getChunkSource().addRegionTicket(TicketType.PLUGIN, chunkPos, 0, Unit.INSTANCE); // Paper
        world.getChunkSource().getChunk(x, z, ChunkStatus.FULL, true);
        return true;
        // Paper end
    }

    @Override
    public boolean isChunkLoaded(Chunk chunk) {
        Preconditions.checkArgument(chunk != null, "null chunk");

        return this.isChunkLoaded(chunk.getX(), chunk.getZ());
    }

    @Override
    public void loadChunk(Chunk chunk) {
        Preconditions.checkArgument(chunk != null, "null chunk");

        this.loadChunk(chunk.getX(), chunk.getZ());
        ((CraftChunk) this.getChunkAt(chunk.getX(), chunk.getZ())).getHandle().bukkitChunk = chunk;
    }

    @Override
    public boolean addPluginChunkTicket(int x, int z, Plugin plugin) {
        Preconditions.checkArgument(plugin != null, "null plugin");
        Preconditions.checkArgument(plugin.isEnabled(), "plugin is not enabled");

        DistanceManager chunkDistanceManager = this.world.getChunkSource().chunkMap.distanceManager;

        if (chunkDistanceManager.addTicketAtLevel(TicketType.PLUGIN_TICKET, new ChunkPos(x, z), 31, plugin)) { // keep in-line with force loading, add at level 31
            this.getChunkAt(x, z); // ensure loaded
            return true;
        }

        return false;
    }

    @Override
    public boolean removePluginChunkTicket(int x, int z, Plugin plugin) {
        Preconditions.checkNotNull(plugin, "null plugin");

        DistanceManager chunkDistanceManager = this.world.getChunkSource().chunkMap.distanceManager;
        return chunkDistanceManager.removeTicketAtLevel(TicketType.PLUGIN_TICKET, new ChunkPos(x, z), 31, plugin); // keep in-line with force loading, remove at level 31
    }

    @Override
    public void removePluginChunkTickets(Plugin plugin) {
        Preconditions.checkNotNull(plugin, "null plugin");

        DistanceManager chunkDistanceManager = this.world.getChunkSource().chunkMap.distanceManager;
        chunkDistanceManager.removeAllTicketsFor(TicketType.PLUGIN_TICKET, 31, plugin); // keep in-line with force loading, remove at level 31
    }

    @Override
    public Collection<Plugin> getPluginChunkTickets(int x, int z) {
        DistanceManager chunkDistanceManager = this.world.getChunkSource().chunkMap.distanceManager;
        SortedArraySet<Ticket<?>> tickets = chunkDistanceManager.tickets.get(ChunkPos.asLong(x, z));

        if (tickets == null) {
            return Collections.emptyList();
        }

        ImmutableList.Builder<Plugin> ret = ImmutableList.builder();
        for (Ticket<?> ticket : tickets) {
            if (ticket.getType() == TicketType.PLUGIN_TICKET) {
                ret.add((Plugin) ticket.key);
            }
        }

        return ret.build();
    }

    @Override
    public Map<Plugin, Collection<Chunk>> getPluginChunkTickets() {
        Map<Plugin, ImmutableList.Builder<Chunk>> ret = new HashMap<>();
        DistanceManager chunkDistanceManager = this.world.getChunkSource().chunkMap.distanceManager;

        for (Long2ObjectMap.Entry<SortedArraySet<Ticket<?>>> chunkTickets : chunkDistanceManager.tickets.long2ObjectEntrySet()) {
            long chunkKey = chunkTickets.getLongKey();
            SortedArraySet<Ticket<?>> tickets = chunkTickets.getValue();

            Chunk chunk = null;
            for (Ticket<?> ticket : tickets) {
                if (ticket.getType() != TicketType.PLUGIN_TICKET) {
                    continue;
                }

                if (chunk == null) {
                    chunk = this.getChunkAt(ChunkPos.getX(chunkKey), ChunkPos.getZ(chunkKey));
                }

                ret.computeIfAbsent((Plugin) ticket.key, (key) -> ImmutableList.builder()).add(chunk);
            }
        }

        return ret.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> entry.getValue().build()));
    }

    @Override
    public boolean isChunkForceLoaded(int x, int z) {
        return this.getHandle().getForcedChunks().contains(ChunkPos.asLong(x, z));
    }

    @Override
    public void setChunkForceLoaded(int x, int z, boolean forced) {
        this.getHandle().setChunkForced(x, z, forced);
    }

    @Override
    public Collection<Chunk> getForceLoadedChunks() {
        Set<Chunk> chunks = new HashSet<>();

        for (long coord : this.getHandle().getForcedChunks()) {
            chunks.add(this.getChunkAt(ChunkPos.getX(coord), ChunkPos.getZ(coord)));
        }

        return Collections.unmodifiableCollection(chunks);
    }

    public ServerLevel getHandle() {
        return this.world;
    }

    @Override
    public org.bukkit.entity.Item dropItem(Location loc, ItemStack item) {
        return this.dropItem(loc, item, null);
    }

    @Override
    public org.bukkit.entity.Item dropItem(Location loc, ItemStack item, Consumer<org.bukkit.entity.Item> function) {
        Validate.notNull(item, "Cannot drop a Null item.");
        ItemEntity entity = new ItemEntity(this.world, loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
        entity.pickupDelay = 10;
        if (function != null) {
            function.accept((org.bukkit.entity.Item) entity.getBukkitEntity());
        }
        this.world.addFreshEntity(entity, SpawnReason.CUSTOM);
        return (org.bukkit.entity.Item) entity.getBukkitEntity();
    }

    @Override
    public org.bukkit.entity.Item dropItemNaturally(Location loc, ItemStack item) {
        return this.dropItemNaturally(loc, item, null);
    }

    @Override
    public org.bukkit.entity.Item dropItemNaturally(Location loc, ItemStack item, Consumer<org.bukkit.entity.Item> function) {
        double xs = (world.random.nextFloat() * 0.5F) + 0.25D;
        double ys = (world.random.nextFloat() * 0.5F) + 0.25D;
        double zs = (world.random.nextFloat() * 0.5F) + 0.25D;
        loc = loc.clone();
        loc.setX(loc.getX() + xs);
        loc.setY(loc.getY() + ys);
        loc.setZ(loc.getZ() + zs);
        return this.dropItem(loc, item, function);
    }

    @Override
    public Arrow spawnArrow(Location loc, Vector velocity, float speed, float spread) {
        return this.spawnArrow(loc, velocity, speed, spread, Arrow.class);
    }

    @Override
    public <T extends AbstractArrow> T spawnArrow(Location loc, Vector velocity, float speed, float spread, Class<T> clazz) {
        Validate.notNull(loc, "Can not spawn arrow with a null location");
        Validate.notNull(velocity, "Can not spawn arrow with a null velocity");
        Validate.notNull(clazz, "Can not spawn an arrow with no class");

        net.minecraft.world.entity.projectile.AbstractArrow arrow;
        if (TippedArrow.class.isAssignableFrom(clazz)) {
            arrow = EntityType.ARROW.create(world);
            ((net.minecraft.world.entity.projectile.Arrow) arrow).setPotionType(CraftPotionUtil.fromBukkit(new PotionData(PotionType.WATER, false, false)));
        } else if (SpectralArrow.class.isAssignableFrom(clazz)) {
            arrow = EntityType.SPECTRAL_ARROW.create(world);
        } else if (Trident.class.isAssignableFrom(clazz)) {
            arrow = EntityType.TRIDENT.create(world);
        } else {
            arrow = EntityType.ARROW.create(world);
        }

        arrow.moveTo(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        arrow.shoot(velocity.getX(), velocity.getY(), velocity.getZ(), speed, spread);
        this.world.addFreshEntity(arrow);
        return (T) arrow.getBukkitEntity();
    }

    @Override
    public LightningStrike strikeLightning(Location loc) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
        lightning.moveTo(loc.getX(), loc.getY(), loc.getZ());
        this.world.strikeLightning(lightning, LightningStrikeEvent.Cause.CUSTOM);
        return (LightningStrike) lightning.getBukkitEntity();
    }

    @Override
    public LightningStrike strikeLightningEffect(Location loc) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
        lightning.moveTo(loc.getX(), loc.getY(), loc.getZ());
        lightning.setVisualOnly(true);
        this.world.strikeLightning(lightning, LightningStrikeEvent.Cause.CUSTOM);
        return (LightningStrike) lightning.getBukkitEntity();
    }

    // Paper start
    @Override
    public Location findLightningRod(Location location) {
        return this.world.findLightningRod(net.minecraft.server.MCUtil.toBlockPosition(location))
            .map(blockPos -> net.minecraft.server.MCUtil.toLocation(this.world, blockPos)
                // get the actual rod pos
                .subtract(0, 1, 0))
            .orElse(null);
    }

    @Override
    public Location findLightningTarget(Location location) {
        final BlockPos pos = this.world.findLightningTargetAround(net.minecraft.server.MCUtil.toBlockPosition(location), true);
        return pos == null ? null : net.minecraft.server.MCUtil.toLocation(this.world, pos);
    }
    // Paper end

    @Override
    public boolean generateTree(Location loc, TreeType type) {
        return generateTree(loc, CraftWorld.rand, type);
    }

    @Override
    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        world.captureTreeGeneration = true;
        world.captureBlockStates = true;
        boolean grownTree = this.generateTree(loc, type);
        world.captureBlockStates = false;
        world.captureTreeGeneration = false;
        if (grownTree) { // Copy block data to delegate
            for (BlockState blockstate : world.capturedBlockStates.values()) {
                BlockPos position = ((CraftBlockState) blockstate).getPosition();
                net.minecraft.world.level.block.state.BlockState oldBlock = this.world.getBlockState(position);
                int flag = ((CraftBlockState) blockstate).getFlag();
                delegate.setBlockData(blockstate.getX(), blockstate.getY(), blockstate.getZ(), blockstate.getBlockData());
                net.minecraft.world.level.block.state.BlockState newBlock = this.world.getBlockState(position);
                this.world.notifyAndUpdatePhysics(position, null, oldBlock, newBlock, newBlock, flag, 512);
            }
            world.capturedBlockStates.clear();
            return true;
        } else {
            world.capturedBlockStates.clear();
            return false;
        }
    }

    @Override
    public String getName() {
        return world.serverLevelData.getLevelName();
    }

    @Override
    public UUID getUID() {
        return world.uuid;
    }

    @Override
    public String toString() {
        return "CraftWorld{name=" + this.getName() + '}';
    }

    @Override
    public long getTime() {
        long time = this.getFullTime() % 24000;
        if (time < 0) time += 24000;
        return time;
    }

    @Override
    public void setTime(long time) {
        long margin = (time - this.getFullTime()) % 24000;
        if (margin < 0) margin += 24000;
        this.setFullTime(this.getFullTime() + margin);
    }

    @Override
    public long getFullTime() {
        return this.world.getDayTime();
    }

    @Override
    public void setFullTime(long time) {
        // Notify anyone who's listening
        TimeSkipEvent event = new TimeSkipEvent(this, TimeSkipEvent.SkipReason.CUSTOM, time - this.world.getDayTime());
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        this.world.setDayTime(this.world.getDayTime() + event.getSkipAmount());

        // Forces the client to update to the new time immediately
        for (Player p : this.getPlayers()) {
            CraftPlayer cp = (CraftPlayer) p;
            if (cp.getHandle().connection == null) continue;

            cp.getHandle().connection.send(new ClientboundSetTimePacket(cp.getHandle().level.getGameTime(), cp.getHandle().getPlayerTime(), cp.getHandle().level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
        }
    }

    // Paper start
    @Override
    public boolean isDayTime() {
        return getHandle().isDay();
    }
    // Paper end

    @Override
    public long getGameTime() {
        return world.levelData.getGameTime();
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power) {
        return this.createExplosion(x, y, z, power, false, true);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
        return this.createExplosion(x, y, z, power, setFire, true);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
        return this.createExplosion(x, y, z, power, setFire, breakBlocks, null);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks, Entity source) {
        return !this.world.explode(source == null ? null : ((CraftEntity) source).getHandle(), x, y, z, power, setFire, breakBlocks ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE).wasCanceled;
    }
    // Paper start
    @Override
    public boolean createExplosion(Entity source, Location loc, float power, boolean setFire, boolean breakBlocks) {
        return !world.explode(source != null ? ((org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity) source).getHandle() : null, loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE).wasCanceled;
    }
    // Paper end

    @Override
    public boolean createExplosion(Location loc, float power) {
        return this.createExplosion(loc, power, false);
    }

    @Override
    public boolean createExplosion(Location loc, float power, boolean setFire) {
        return this.createExplosion(loc, power, setFire, true);
    }

    @Override
    public boolean createExplosion(Location loc, float power, boolean setFire, boolean breakBlocks) {
        return this.createExplosion(loc, power, setFire, breakBlocks, null);
    }

    @Override
    public boolean createExplosion(Location loc, float power, boolean setFire, boolean breakBlocks, Entity source) {
        Preconditions.checkArgument(loc != null, "Location is null");
        Preconditions.checkArgument(this.equals(loc.getWorld()), "Location not in world");

        return this.createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks, source);
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public Block getBlockAt(Location location) {
        return this.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public int getHighestBlockYAt(Location location) {
        return this.getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
    }

    @Override
    public Chunk getChunkAt(Location location) {
        return this.getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @Override
    public ChunkGenerator getGenerator() {
        return this.generator;
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        return this.biomeProvider;
    }

    @Override
    public List<BlockPopulator> getPopulators() {
        return this.populators;
    }

    @Override
    public Block getHighestBlockAt(int x, int z) {
        return this.getBlockAt(x, this.getHighestBlockYAt(x, z), z);
    }

    @Override
    public Block getHighestBlockAt(Location location) {
        return this.getHighestBlockAt(location.getBlockX(), location.getBlockZ());
    }

    @Override
    public int getHighestBlockYAt(int x, int z, org.bukkit.HeightMap heightMap) {
        // Transient load for this tick
        return this.world.getChunk(x >> 4, z >> 4).getHeight(CraftHeightMap.toNMS(heightMap), x, z);
    }

    @Override
    public int getHighestBlockYAt(Location location, org.bukkit.HeightMap heightMap) {
        return this.getHighestBlockYAt(location.getBlockX(), location.getBlockZ(), heightMap);
    }

    @Override
    public Block getHighestBlockAt(int x, int z, org.bukkit.HeightMap heightMap) {
        return this.getBlockAt(x, this.getHighestBlockYAt(x, z, heightMap), z);
    }

    @Override
    public Block getHighestBlockAt(Location location, org.bukkit.HeightMap heightMap) {
        return this.getHighestBlockAt(location.getBlockX(), location.getBlockZ(), heightMap);
    }

    @Override
    public Biome getBiome(int x, int z) {
        return getBiome(x, 0, z);
    }

    @Override
    public void setBiome(int x, int z, Biome bio) {
        for (int y = this.getMinHeight(); y < this.getMaxHeight(); y++) {
            setBiome(x, y, z, bio);
        }
    }

    @Override
    public void setBiome(int x, int y, int z, net.minecraft.world.level.biome.Biome bb) {
        BlockPos pos = new BlockPos(x, 0, z);
        if (this.world.hasChunkAt(pos)) {
            net.minecraft.world.level.chunk.LevelChunk chunk = this.world.getChunkAt(pos);

            if (chunk != null) {
                chunk.setBiome(x >> 2, y >> 2, z >> 2, bb);

                chunk.setUnsaved(true); // SPIGOT-2890
            }
        }
    }

    @Override
    public double getTemperature(int x, int z) {
        return this.getTemperature(x, 0, z);
    }

    @Override
    public double getTemperature(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return this.world.getNoiseBiome(x >> 2, y >> 2, z >> 2).getTemperature(pos);
    }

    @Override
    public double getHumidity(int x, int z) {
        return this.getHumidity(x, 0, z);
    }

    @Override
    public double getHumidity(int x, int y, int z) {
        return this.world.getNoiseBiome(x >> 2, y >> 2, z >> 2).getDownfall();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Deprecated
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
        return (Collection<T>) getEntitiesByClasses(classes);
    }

    @Override
    public Iterable<net.minecraft.world.entity.Entity> getNMSEntities() {
        return this.getHandle().getEntities().getAll();
    }

    @Override
    public void addEntityToWorld(net.minecraft.world.entity.Entity entity, SpawnReason reason) {
        this.getHandle().addFreshEntity(entity, reason);
    }

    @Override
    public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
        return this.getNearbyEntities(location, x, y, z, null);
    }

    @Override
    public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z, Predicate<Entity> filter) {
        Validate.notNull(location, "Location is null!");
        Validate.isTrue(this.equals(location.getWorld()), "Location is from different world!");

        BoundingBox aabb = BoundingBox.of(location, x, y, z);
        return this.getNearbyEntities(aabb, filter);
    }

    @Override
    public Collection<Entity> getNearbyEntities(BoundingBox boundingBox) {
        return this.getNearbyEntities(boundingBox, null);
    }

    @Override
    public Collection<Entity> getNearbyEntities(BoundingBox boundingBox, Predicate<Entity> filter) {
        org.spigotmc.AsyncCatcher.catchOp("getNearbyEntities"); // Spigot
        Validate.notNull(boundingBox, "Bounding box is null!");

        AABB bb = new AABB(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
        List<net.minecraft.world.entity.Entity> entityList = this.getHandle().getEntities((net.minecraft.world.entity.Entity) null, bb, Predicates.alwaysTrue());
        List<Entity> bukkitEntityList = new ArrayList<org.bukkit.entity.Entity>(entityList.size());

        for (net.minecraft.world.entity.Entity entity : entityList) {
            Entity bukkitEntity = entity.getBukkitEntity();
            if (filter == null || filter.test(bukkitEntity)) {
                bukkitEntityList.add(bukkitEntity);
            }
        }

        return bukkitEntityList;
    }

    @Override
    public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance) {
        return this.rayTraceEntities(start, direction, maxDistance, null);
    }

    @Override
    public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize) {
        return this.rayTraceEntities(start, direction, maxDistance, raySize, null);
    }

    @Override
    public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, Predicate<Entity> filter) {
        return this.rayTraceEntities(start, direction, maxDistance, 0.0D, filter);
    }

    @Override
    public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize, Predicate<Entity> filter) {
        Validate.notNull(start, "Start location is null!");
        Validate.isTrue(this.equals(start.getWorld()), "Start location is from different world!");
        start.checkFinite();

        Validate.notNull(direction, "Direction is null!");
        direction.checkFinite();

        Validate.isTrue(direction.lengthSquared() > 0, "Direction's magnitude is 0!");

        if (maxDistance < 0.0D) {
            return null;
        }

        Vector startPos = start.toVector();
        Vector dir = direction.clone().normalize().multiply(maxDistance);
        BoundingBox aabb = BoundingBox.of(startPos, startPos).expandDirectional(dir).expand(raySize);
        Collection<Entity> entities = this.getNearbyEntities(aabb, filter);

        Entity nearestHitEntity = null;
        RayTraceResult nearestHitResult = null;
        double nearestDistanceSq = Double.MAX_VALUE;

        for (Entity entity : entities) {
            BoundingBox boundingBox = entity.getBoundingBox().expand(raySize);
            RayTraceResult hitResult = boundingBox.rayTrace(startPos, direction, maxDistance);

            if (hitResult != null) {
                double distanceSq = startPos.distanceSquared(hitResult.getHitPosition());

                if (distanceSq < nearestDistanceSq) {
                    nearestHitEntity = entity;
                    nearestHitResult = hitResult;
                    nearestDistanceSq = distanceSq;
                }
            }
        }

        return (nearestHitEntity == null) ? null : new RayTraceResult(nearestHitResult.getHitPosition(), nearestHitEntity, nearestHitResult.getHitBlockFace());
    }

    @Override
    public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance) {
        return this.rayTraceBlocks(start, direction, maxDistance, FluidCollisionMode.NEVER, false);
    }

    @Override
    public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode) {
        return this.rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, false);
    }

    @Override
    public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks) {
        Validate.notNull(start, "Start location is null!");
        Validate.isTrue(this.equals(start.getWorld()), "Start location is from different world!");
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
        HitResult nmsHitResult = this.getHandle().clip(new ClipContext(startPos, endPos, ignorePassableBlocks ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE, CraftFluidCollisionMode.toNMS(fluidCollisionMode), null));

        return CraftRayTraceResult.fromNMS(this, nmsHitResult);
    }

    @Override
    public RayTraceResult rayTrace(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, Predicate<Entity> filter) {
        RayTraceResult blockHit = this.rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, ignorePassableBlocks);
        Vector startVec = null;
        double blockHitDistance = maxDistance;

        // limiting the entity search range if we found a block hit:
        if (blockHit != null) {
            startVec = start.toVector();
            blockHitDistance = startVec.distance(blockHit.getHitPosition());
        }

        RayTraceResult entityHit = this.rayTraceEntities(start, direction, blockHitDistance, raySize, filter);
        if (blockHit == null) {
            return entityHit;
        }

        if (entityHit == null) {
            return blockHit;
        }

        // Cannot be null as blockHit == null returns above
        double entityHitDistanceSquared = startVec.distanceSquared(entityHit.getHitPosition());
        if (entityHitDistanceSquared < (blockHitDistance * blockHitDistance)) {
            return entityHit;
        }

        return blockHit;
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> list = new ArrayList<Player>(this.world.players().size());

        for (net.minecraft.world.entity.player.Player human : this.world.players()) {
            HumanEntity bukkitEntity = human.getBukkitEntity();

            if ((bukkitEntity != null) && (bukkitEntity instanceof Player)) {
                list.add((Player) bukkitEntity);
            }
        }

        return list;
    }

    // Paper start - getEntity by UUID API
    @Override
    public Entity getEntity(UUID uuid) {
        Validate.notNull(uuid, "UUID cannot be null");
        net.minecraft.world.entity.Entity entity = world.getEntity(uuid);
        return entity == null ? null : entity.getBukkitEntity();
    }
    // Paper end

    @Override
    public void save() {
        org.spigotmc.AsyncCatcher.catchOp("world save"); // Spigot
        this.server.checkSaveState();
        boolean oldSave = world.noSave;

        world.noSave = false;
        this.world.save(null, false, false);

        world.noSave = oldSave;
    }

    @Override
    public boolean isAutoSave() {
        return !world.noSave;
    }

    @Override
    public void setAutoSave(boolean value) {
        world.noSave = !value;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.getHandle().serverLevelData.setDifficulty(net.minecraft.world.Difficulty.byId(difficulty.getValue()));
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.getByValue(this.getHandle().getDifficulty().ordinal());
    }

    public BlockMetadataStore getBlockMetadata() {
        return this.blockMetadata;
    }

    @Override
    public boolean hasStorm() {
        return world.levelData.isRaining();
    }

    @Override
    public void setStorm(boolean hasStorm) {
        world.serverLevelData.setRaining(hasStorm, org.bukkit.event.weather.WeatherChangeEvent.Cause.PLUGIN); // Paper
        this.setWeatherDuration(0); // Reset weather duration (legacy behaviour)
        this.setClearWeatherDuration(0); // Reset clear weather duration (reset "/weather clear" commands)
    }

    @Override
    public int getWeatherDuration() {
        return world.serverLevelData.getRainTime();
    }

    @Override
    public void setWeatherDuration(int duration) {
        world.serverLevelData.setRainTime(duration);
    }

    @Override
    public boolean isThundering() {
        return world.levelData.isThundering();
    }

    @Override
    public void setThundering(boolean thundering) {
        world.serverLevelData.setThundering(thundering, org.bukkit.event.weather.ThunderChangeEvent.Cause.PLUGIN); // Paper
        this.setThunderDuration(0); // Reset weather duration (legacy behaviour)
        this.setClearWeatherDuration(0); // Reset clear weather duration (reset "/weather clear" commands)
    }

    @Override
    public int getThunderDuration() {
        return world.serverLevelData.getThunderTime();
    }

    @Override
    public void setThunderDuration(int duration) {
        world.serverLevelData.setThunderTime(duration);
    }

    @Override
    public boolean isClearWeather() {
        return !this.hasStorm() && !this.isThundering();
    }

    @Override
    public void setClearWeatherDuration(int duration) {
        world.serverLevelData.setClearWeatherTime(duration);
    }

    @Override
    public int getClearWeatherDuration() {
        return world.serverLevelData.getClearWeatherTime();
    }

    @Override
    public long getSeed() {
        return this.world.getSeed();
    }

    @Override
    public boolean getPVP() {
        return world.pvpMode;
    }

    @Override
    public void setPVP(boolean pvp) {
        world.pvpMode = pvp;
    }

    public void playEffect(Player player, Effect effect, int data) {
        this.playEffect(player.getLocation(), effect, data, 0);
    }

    @Override
    public void playEffect(Location location, Effect effect, int data) {
        this.playEffect(location, effect, data, 64);
    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data) {
        this.playEffect(loc, effect, data, 64);
    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data, int radius) {
        if (data != null) {
            Validate.isTrue(effect.getData() != null && effect.getData().isAssignableFrom(data.getClass()), "Wrong kind of data for this effect!");
        } else {
            // Special case: the axis is optional for ELECTRIC_SPARK
            Validate.isTrue(effect.getData() == null || effect == Effect.ELECTRIC_SPARK, "Wrong kind of data for this effect!");
        }

        int datavalue = CraftEffect.getDataValue(effect, data);
        this.playEffect(loc, effect, datavalue, radius);
    }

    @Override
    public void playEffect(Location location, Effect effect, int data, int radius) {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(effect, "Effect cannot be null");
        Validate.notNull(location.getWorld(), "World cannot be null");
        int packetData = effect.getId();
        ClientboundLevelEventPacket packet = new ClientboundLevelEventPacket(packetData, new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()), data, false);
        int distance;
        radius *= radius;

        for (Player player : this.getPlayers()) {
            if (((CraftPlayer) player).getHandle().connection == null) continue;
            if (!location.getWorld().equals(player.getWorld())) continue;

            distance = (int) player.getLocation().distanceSquared(location);
            if (distance <= radius) {
                ((CraftPlayer) player).getHandle().connection.send(packet);
            }
        }
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, MaterialData data) throws IllegalArgumentException {
        Validate.notNull(data, "MaterialData cannot be null");
        return this.spawnFallingBlock(location, data.getItemType(), data.getData());
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, org.bukkit.Material material, byte data) throws IllegalArgumentException {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(material, "Material cannot be null");
        Validate.isTrue(material.isBlock(), "Material must be a block");

        FallingBlockEntity entity = new FallingBlockEntity(this.world, location.getX(), location.getY(), location.getZ(), CraftMagicNumbers.getBlock(material).defaultBlockState());
        entity.time = 1;

        this.world.addFreshEntity(entity, SpawnReason.CUSTOM);
        return (FallingBlock) entity.getBukkitEntity();
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, BlockData data) throws IllegalArgumentException {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(data, "Material cannot be null");

        FallingBlockEntity entity = new FallingBlockEntity(this.world, location.getX(), location.getY(), location.getZ(), ((CraftBlockData) data).getState());
        entity.time = 1;

        this.world.addFreshEntity(entity, SpawnReason.CUSTOM);
        return (FallingBlock) entity.getBukkitEntity();
    }

    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
        return CraftChunk.getEmptyChunkSnapshot(x, z, this, includeBiome, includeBiomeTempRain);
    }

    @Override
    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
        this.world.setSpawnSettings(allowMonsters, allowAnimals);
    }

    @Override
    public boolean getAllowAnimals() {
        return this.world.getChunkSource().spawnFriendlies;
    }

    @Override
    public boolean getAllowMonsters() {
        return this.world.getChunkSource().spawnEnemies;
    }

    @Override
    public int getMinHeight() {
        return this.world.getMinBuildHeight();
    }

    @Override
    public int getMaxHeight() {
        return this.world.getMaxBuildHeight();
    }

    @Override
    public int getLogicalHeight() {
        return this.world.dimensionType().logicalHeight();
    }

    @Override
    public boolean isNatural() {
        return this.world.dimensionType().natural();
    }

    @Override
    public boolean isBedWorks() {
        return this.world.dimensionType().bedWorks();
    }

    @Override
    public boolean hasSkyLight() {
        return this.world.dimensionType().hasSkyLight();
    }

    @Override
    public boolean hasCeiling() {
        return this.world.dimensionType().hasCeiling();
    }

    @Override
    public boolean isPiglinSafe() {
        return this.world.dimensionType().piglinSafe();
    }

    @Override
    public boolean isRespawnAnchorWorks() {
        return this.world.dimensionType().respawnAnchorWorks();
    }

    @Override
    public boolean hasRaids() {
        return this.world.dimensionType().hasRaids();
    }

    @Override
    public boolean isUltraWarm() {
        return this.world.dimensionType().ultraWarm();
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public boolean getKeepSpawnInMemory() {
        return world.keepSpawnInMemory;
    }

    @Override
    public void setKeepSpawnInMemory(boolean keepLoaded) {
        // Paper start - Configurable spawn radius
        if (keepLoaded == world.keepSpawnInMemory) {
            // do nothing, nothing has changed
            return;
        }
        this.world.keepSpawnInMemory = keepLoaded;
        // Grab the worlds spawn chunk
        BlockPos chunkcoordinates = this.world.getSharedSpawnPos();
        if (keepLoaded) {
            this.world.addTicketsForSpawn(this.world.paperConfig.keepLoadedRange, chunkcoordinates);
        } else {
            // TODO: doesn't work well if spawn changed.... // Paper - resolved
            this.world.removeTicketsForSpawn(this.world.paperConfig.keepLoadedRange, chunkcoordinates);
        }
        // Paper end
    }

    @Override
    public int hashCode() {
        return this.getUID().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final CraftWorld other = (CraftWorld) obj;

        return this.getUID() == other.getUID();
    }

    @Override
    public File getWorldFolder() {
        return world.convertable.getLevelPath(LevelResource.ROOT).toFile().getParentFile();
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(this.server.getMessenger(), source, channel, message);

        for (Player player : this.getPlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        Set<String> result = new HashSet<String>();

        for (Player player : this.getPlayers()) {
            result.addAll(player.getListeningPluginChannels());
        }

        return result;
    }

    @Override
    public org.bukkit.WorldType getWorldType() {
        return this.world.isFlat() ? org.bukkit.WorldType.FLAT : org.bukkit.WorldType.NORMAL;
    }

    @Override
    public boolean canGenerateStructures() {
        return world.serverLevelData.worldGenSettings().generateFeatures();
    }

    @Override
    public boolean isHardcore() {
        return this.world.getLevelData().isHardcore();
    }

    @Override
    public void setHardcore(boolean hardcore) {
        world.serverLevelData.settings.hardcore = hardcore;
    }

    @Override
    public long getTicksPerAnimalSpawns() {
        return world.ticksPerAnimalSpawns;
    }

    @Override
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
        world.ticksPerAnimalSpawns = ticksPerAnimalSpawns;
    }

    @Override
    public long getTicksPerMonsterSpawns() {
        return world.ticksPerMonsterSpawns;
    }

    @Override
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
        world.ticksPerMonsterSpawns = ticksPerMonsterSpawns;
    }

    @Override
    public long getTicksPerWaterSpawns() {
        return world.ticksPerWaterSpawns;
    }

    @Override
    public void setTicksPerWaterSpawns(int ticksPerWaterSpawns) {
        world.ticksPerWaterSpawns = ticksPerWaterSpawns;
    }

    @Override
    public long getTicksPerWaterAmbientSpawns() {
        return world.ticksPerWaterAmbientSpawns;
    }

    @Override
    public void setTicksPerWaterAmbientSpawns(int ticksPerWaterAmbientSpawns) {
        world.ticksPerWaterAmbientSpawns = ticksPerWaterAmbientSpawns;
    }

    @Override
    public long getTicksPerWaterUndergroundCreatureSpawns() {
        return world.ticksPerWaterUndergroundCreatureSpawns;
    }

    @Override
    public void setTicksPerWaterUndergroundCreatureSpawns(int ticksPerWaterUndergroundCreatureSpawns) {
        world.ticksPerWaterUndergroundCreatureSpawns = ticksPerWaterUndergroundCreatureSpawns;
    }

    @Override
    public long getTicksPerAmbientSpawns() {
        return world.ticksPerAmbientSpawns;
    }

    @Override
    public void setTicksPerAmbientSpawns(int ticksPerAmbientSpawns) {
        world.ticksPerAmbientSpawns = ticksPerAmbientSpawns;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getWorldMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getWorldMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getWorldMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getWorldMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public int getMonsterSpawnLimit() {
        if (this.monsterSpawn < 0) {
            return this.server.getMonsterSpawnLimit();
        }

        return this.monsterSpawn;
    }

    @Override
    public void setMonsterSpawnLimit(int limit) {
        this.monsterSpawn = limit;
    }

    @Override
    public int getAnimalSpawnLimit() {
        if (this.animalSpawn < 0) {
            return this.server.getAnimalSpawnLimit();
        }

        return this.animalSpawn;
    }

    @Override
    public void setAnimalSpawnLimit(int limit) {
        this.animalSpawn = limit;
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        if (this.waterAnimalSpawn < 0) {
            return this.server.getWaterAnimalSpawnLimit();
        }

        return this.waterAnimalSpawn;
    }

    @Override
    public void setWaterAnimalSpawnLimit(int limit) {
        this.waterAnimalSpawn = limit;
    }

    @Override
    public int getWaterAmbientSpawnLimit() {
        if (this.waterAmbientSpawn < 0) {
            return this.server.getWaterAmbientSpawnLimit();
        }

        return this.waterAmbientSpawn;
    }

    @Override
    public void setWaterAmbientSpawnLimit(int limit) {
        this.waterAmbientSpawn = limit;
    }

    @Override
    public int getWaterUndergroundCreatureSpawnLimit() {
        if (this.waterUndergroundCreatureSpawn < 0) {
            return this.server.getWaterUndergroundCreatureSpawnLimit();
        }

        return this.waterUndergroundCreatureSpawn;
    }

    @Override
    public void setWaterUndergroundCreatureSpawnLimit(int limit) {
        this.waterUndergroundCreatureSpawn = limit;
    }

    @Override
    public int getAmbientSpawnLimit() {
        if (this.ambientSpawn < 0) {
            return this.server.getAmbientSpawnLimit();
        }

        return this.ambientSpawn;
    }

    @Override
    public void setAmbientSpawnLimit(int limit) {
        this.ambientSpawn = limit;
    }

    @Override
    public void playSound(Location loc, Sound sound, float volume, float pitch) {
        this.playSound(loc, sound, org.bukkit.SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void playSound(Location loc, String sound, float volume, float pitch) {
        this.playSound(loc, sound, org.bukkit.SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void playSound(Location loc, Sound sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        if (loc == null || sound == null || category == null) return;

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        this.getHandle().playSound(null, x, y, z, CraftSound.getSoundEffect(sound), SoundSource.valueOf(category.name()), volume, pitch);
    }

    @Override
    public void playSound(Location loc, String sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        if (loc == null || sound == null || category == null) return;

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        ClientboundCustomSoundPacket packet = new ClientboundCustomSoundPacket(new ResourceLocation(sound), SoundSource.valueOf(category.name()), new Vec3(x, y, z), volume, pitch);
        this.world.getServer().getPlayerList().broadcast(null, x, y, z, volume > 1.0F ? 16.0F * volume : 16.0D, this.world.dimension(), packet);
    }

    private static Map<String, GameRules.Key<?>> gamerules;
    public static synchronized Map<String, GameRules.Key<?>> getGameRulesNMS() {
        if (CraftWorld.gamerules != null) {
            return CraftWorld.gamerules;
        }

        Map<String, GameRules.Key<?>> gamerules = new HashMap<>();
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            @Override
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                gamerules.put(key.getId(), key);
            }
        });

        return CraftWorld.gamerules = gamerules;
    }

    private static Map<String, GameRules.Type<?>> gameruleDefinitions;
    public static synchronized Map<String, GameRules.Type<?>> getGameRuleDefinitions() {
        if (CraftWorld.gameruleDefinitions != null) {
            return CraftWorld.gameruleDefinitions;
        }

        Map<String, GameRules.Type<?>> gameruleDefinitions = new HashMap<>();
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            @Override
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                gameruleDefinitions.put(key.getId(), type);
            }
        });

        return CraftWorld.gameruleDefinitions = gameruleDefinitions;
    }

    @Override
    public String getGameRuleValue(String rule) {
        // In method contract for some reason
        if (rule == null) {
            return null;
        }

        GameRules.Value<?> value = this.getHandle().getGameRules().getRule(CraftWorld.getGameRulesNMS().get(rule));
        return value != null ? value.toString() : "";
    }

    @Override
    public boolean setGameRuleValue(String rule, String value) {
        // No null values allowed
        if (rule == null || value == null) return false;

        if (!this.isGameRule(rule)) return false;

        // Paper start
        GameRule<?> gameRule = GameRule.getByName(rule);
        io.papermc.paper.event.world.WorldGameRuleChangeEvent event = new io.papermc.paper.event.world.WorldGameRuleChangeEvent(this, null, gameRule, value);
        if (!event.callEvent()) return false;
        // Paper end
        GameRules.Value<?> handle = this.getHandle().getGameRules().getRule(CraftWorld.getGameRulesNMS().get(rule));
        handle.deserialize(event.getValue()); // Paper
        handle.onChanged(this.getHandle().getServer());
        return true;
    }

    @Override
    public String[] getGameRules() {
        return CraftWorld.getGameRulesNMS().keySet().toArray(new String[CraftWorld.getGameRulesNMS().size()]);
    }

    @Override
    public boolean isGameRule(String rule) {
        Validate.isTrue(rule != null && !rule.isEmpty(), "Rule cannot be null nor empty");
        return CraftWorld.getGameRulesNMS().containsKey(rule);
    }

    @Override
    public <T> T getGameRuleValue(GameRule<T> rule) {
        Validate.notNull(rule, "GameRule cannot be null");
        return this.convert(rule, this.getHandle().getGameRules().getRule(CraftWorld.getGameRulesNMS().get(rule.getName())));
    }

    @Override
    public <T> T getGameRuleDefault(GameRule<T> rule) {
        Validate.notNull(rule, "GameRule cannot be null");
        return this.convert(rule, CraftWorld.getGameRuleDefinitions().get(rule.getName()).createRule());
    }

    @Override
    public <T> boolean setGameRule(GameRule<T> rule, T newValue) {
        Validate.notNull(rule, "GameRule cannot be null");
        Validate.notNull(newValue, "GameRule value cannot be null");

        if (!this.isGameRule(rule.getName())) return false;

        // Paper start
        io.papermc.paper.event.world.WorldGameRuleChangeEvent event = new io.papermc.paper.event.world.WorldGameRuleChangeEvent(this, null, rule, String.valueOf(newValue));
        if (!event.callEvent()) return false;
        // Paper end
        GameRules.Value<?> handle = this.getHandle().getGameRules().getRule(CraftWorld.getGameRulesNMS().get(rule.getName()));
        handle.deserialize(event.getValue()); // Paper
        handle.onChanged(this.getHandle().getServer());
        return true;
    }

    private <T> T convert(GameRule<T> rule, GameRules.Value<?> value) {
        if (value == null) {
            return null;
        }

        if (value instanceof GameRules.BooleanValue) {
            return rule.getType().cast(((GameRules.BooleanValue) value).get());
        } else if (value instanceof GameRules.IntegerValue) {
            return rule.getType().cast(value.getCommandResult());
        } else {
            throw new IllegalArgumentException("Invalid GameRule type (" + value + ") for GameRule " + rule.getName());
        }
    }

    @Override
    public WorldBorder getWorldBorder() {
        if (this.worldBorder == null) {
            this.worldBorder = new CraftWorldBorder(this);
        }

        return this.worldBorder;
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count) {
        this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count) {
        this.spawnParticle(particle, x, y, z, count, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
        this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
        this.spawnParticle(particle, x, y, z, count, 0, 0, 0, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 1, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, false);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
        this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data, force);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
        // Paper start - Particle API Expansion
        spawnParticle(particle, null, null, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, force);
    }
    public <T> void spawnParticle(Particle particle, List<Player> receivers, Player sender, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
        // Paper end
        if (data != null && !particle.getDataType().isInstance(data)) {
            throw new IllegalArgumentException("data should be " + particle.getDataType() + " got " + data.getClass());
        }
        this.getHandle().sendParticles(
                receivers == null ? getHandle().players() : receivers.stream().map(player -> ((CraftPlayer) player).getHandle()).collect(java.util.stream.Collectors.toList()), // Paper -  Particle API Expansion
                sender != null ? ((CraftPlayer) sender).getHandle() : null, // Sender // Paper - Particle API Expansion
                CraftParticle.toNMS(particle, data), // Particle
                x, y, z, // Position
                count,  // Count
                offsetX, offsetY, offsetZ, // Random offset
                extra, // Speed?
                force
        );

    }

    @Override
    public Location locateNearestStructure(Location origin, StructureType structureType, int radius, boolean findUnexplored) {
        BlockPos originPos = new BlockPos(origin.getX(), origin.getY(), origin.getZ());
        BlockPos nearest = this.getHandle().getChunkSource().getGenerator().findNearestMapFeature(this.getHandle(), StructureFeature.STRUCTURES_REGISTRY.get(structureType.getName()), originPos, radius, findUnexplored);
        return (nearest == null) ? null : new Location(this, nearest.getX(), nearest.getY(), nearest.getZ());
    }

    // Paper start
    @Override
    public Location locateNearestBiome(Location origin, Biome biome, int radius) {
        return this.locateNearestBiome(origin, biome, radius, 8);
    }

    @Override
    public Location locateNearestBiome(Location origin, Biome biome, int radius, int step) {
        BlockPos originPos = new BlockPos(origin.getX(), origin.getY(), origin.getZ());
        BlockPos nearest = getHandle().findNearestBiome(CraftBlock.biomeToBiomeBase(getHandle().registryAccess().registryOrThrow(net.minecraft.core.Registry.BIOME_REGISTRY), biome), originPos, radius, step);
        return (nearest == null) ? null : new Location(this, nearest.getX(), nearest.getY(), nearest.getZ());
    }

    @Override
    public boolean isUltrawarm() {
        return getHandle().dimensionType().ultraWarm();
    }

    @Override
    public double getCoordinateScale() {
        return getHandle().dimensionType().coordinateScale();
    }

    @Override
    public boolean hasSkylight() {
        return getHandle().dimensionType().hasSkyLight();
    }

    @Override
    public boolean hasBedrockCeiling() {
        return getHandle().dimensionType().hasSkyLight();
    }

    @Override
    public boolean doesBedWork() {
        return getHandle().dimensionType().bedWorks();
    }

    @Override
    public boolean doesRespawnAnchorWork() {
        return getHandle().dimensionType().respawnAnchorWorks();
    }

    @Override
    public boolean isFixedTime() {
        return getHandle().dimensionType().hasFixedTime();
    }

    @Override
    public Collection<org.bukkit.Material> getInfiniburn() {
        return com.google.common.collect.Sets.newHashSet(com.google.common.collect.Iterators.transform(getHandle().dimensionType().infiniburn().getValues().iterator(), CraftMagicNumbers::getMaterial));
    }

    @Override
    public void sendGameEvent(Entity sourceEntity, org.bukkit.GameEvent gameEvent, Vector position) {
        getHandle().gameEvent(sourceEntity != null ? ((CraftEntity) sourceEntity).getHandle(): null, net.minecraft.core.Registry.GAME_EVENT.get(org.bukkit.craftbukkit.v1_18_R1.util.CraftNamespacedKey.toMinecraft(gameEvent.getKey())), org.bukkit.craftbukkit.v1_18_R1.util.CraftVector.toBlockPos(position));
    }
    // Paper end

    @Override
    public Raid locateNearestRaid(Location location, int radius) {
        Validate.notNull(location, "Location cannot be null");
        Validate.isTrue(radius >= 0, "Radius cannot be negative");

        Raids persistentRaid = this.world.getRaids();
        net.minecraft.world.entity.raid.Raid raid = persistentRaid.getNearbyRaid(new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()), radius * radius);
        return (raid == null) ? null : new CraftRaid(raid);
    }

    @Override
    public List<Raid> getRaids() {
        Raids persistentRaid = this.world.getRaids();
        return persistentRaid.raidMap.values().stream().map(CraftRaid::new).collect(Collectors.toList());
    }

    @Override
    public DragonBattle getEnderDragonBattle() {
        return (this.getHandle().dragonFight() == null) ? null : new CraftDragonBattle(this.getHandle().dragonFight());
    }
    // Paper start
    @Override
    public java.util.concurrent.CompletableFuture<Chunk> getChunkAtAsync(int x, int z, boolean gen, boolean urgent) {
        if (Bukkit.isPrimaryThread()) {
            net.minecraft.world.level.chunk.LevelChunk immediate = this.world.getChunkSource().getChunkAtIfLoadedImmediately(x, z);
            if (immediate != null) {
                return java.util.concurrent.CompletableFuture.completedFuture(immediate.getBukkitChunk());
            }
        } else {
            java.util.concurrent.CompletableFuture<Chunk> future = new java.util.concurrent.CompletableFuture<Chunk>();
            world.getServer().execute(() -> {
                getChunkAtAsync(x, z, gen, urgent).whenComplete((chunk, err) -> {
                    if (err != null) {
                        future.completeExceptionally(err);
                    } else {
                        future.complete(chunk);
                    }
                });
            });
            return future;
        }

        // Paper start - Chunk priority
        if (!urgent) {
            // If not urgent, at least use a slightly boosted priority
            world.getChunkSource().markHighPriority(new ChunkPos(x, z), 1);
        }
        // Paper end
        return this.world.getChunkSource().getChunkAtAsynchronously(x, z, gen, urgent).thenComposeAsync((either) -> {
            net.minecraft.world.level.chunk.LevelChunk chunk = (net.minecraft.world.level.chunk.LevelChunk) either.left().orElse(null);
            if (chunk != null) addTicket(x, z); // Paper
            return java.util.concurrent.CompletableFuture.completedFuture(chunk == null ? null : chunk.getBukkitChunk());
        }, net.minecraft.server.MinecraftServer.getServer());
    }

    @Override
    public org.bukkit.NamespacedKey getKey() {
        return org.bukkit.craftbukkit.v1_18_R1.util.CraftNamespacedKey.fromMinecraft(world.dimension().location());
    }
    // Paper end

    // Spigot start
    @Override
    public int getViewDistance() {
        return world.spigotConfig.viewDistance;
    }

    @Override
    public int getSimulationDistance() {
        return world.spigotConfig.simulationDistance;
    }

    @Override
    public void setViewDistance(int viewDistance) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public int getNoTickViewDistance() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public void setNoTickViewDistance(int viewDistance) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public int getSendViewDistance() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public void setSendViewDistance(int viewDistance) {
        throw new UnsupportedOperationException(); //TODO
    }
    // Spigot end

    // Spigot start
    private final org.bukkit.World.Spigot spigot = new org.bukkit.World.Spigot()
    {

        @Override
        public LightningStrike strikeLightning(Location loc, boolean isSilent)
        {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create( world );
            lightning.moveTo( loc.getX(), loc.getY(), loc.getZ() );
            lightning.isSilent = isSilent;
            CraftWorld.this.world.strikeLightning( lightning );
            return (LightningStrike) lightning.getBukkitEntity();
        }

        @Override
        public LightningStrike strikeLightningEffect(Location loc, boolean isSilent)
        {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create( world );
            lightning.moveTo( loc.getX(), loc.getY(), loc.getZ() );
            lightning.visualOnly = true;
            lightning.isSilent = isSilent;
            world.strikeLightning( lightning );
            return (LightningStrike) lightning.getBukkitEntity();
        }
    };

    public org.bukkit.World.Spigot spigot()
    {
        return this.spigot;
    }
    // Spigot end

    // Paper start - implement pointers
    @Override
    public net.kyori.adventure.pointer.Pointers pointers() {
        if (this.adventure$pointers == null) {
            this.adventure$pointers = net.kyori.adventure.pointer.Pointers.builder()
                .withDynamic(net.kyori.adventure.identity.Identity.NAME, this::getName)
                .withDynamic(net.kyori.adventure.identity.Identity.UUID, this::getUID)
                .build();
        }

        return this.adventure$pointers;
    }
    // Paper end
}
