package org.bukkit.craftbukkit.v1_17_R1;

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
import java.util.Iterator;
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
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Features;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
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
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.boss.CraftDragonBattle;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.metadata.BlockMetadataStore;
import org.bukkit.craftbukkit.v1_17_R1.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftRayTraceResult;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Cat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cod;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Egg;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.GlowSquid;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illager;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Mule;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Strider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Trident;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zoglin;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.TimeSkipEvent;
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

public class CraftWorld implements World {
    public static final int CUSTOM_DIMENSION_OFFSET = 10;

    private final ServerLevel world;
    private WorldBorder worldBorder;
    private Environment environment;
    private final CraftServer server = (CraftServer) Bukkit.getServer();
    private final ChunkGenerator generator;
    private final List<BlockPopulator> populators = new ArrayList<BlockPopulator>();
    private final BlockMetadataStore blockMetadata = new BlockMetadataStore(this);
    private int monsterSpawn = -1;
    private int animalSpawn = -1;
    private int waterAnimalSpawn = -1;
    private int waterAmbientSpawn = -1;
    private int ambientSpawn = -1;

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
        return net.minecraft.server.MCUtil.ensureMain(() -> {
        // We don't use the full world tile entity list, so we must iterate chunks
        Long2ObjectLinkedOpenHashMap<ChunkHolder> chunks = world.getChunkSource().chunkMap.visibleChunkMap;
        int size = 0;
        for (ChunkHolder playerchunk : chunks.values()) {
            net.minecraft.world.level.chunk.LevelChunk chunk = playerchunk.getTickingChunk();
            if (chunk == null) {
                continue;
            }
            size += chunk.blockEntities.size();
        }
        return size;
        });
    }

    @Override
    public int getTickableTileEntityCount() {
        return world.getTotalTileEntityTickers();
    }

    @Override
    public int getChunkCount() {
        return net.minecraft.server.MCUtil.ensureMain(() -> {
        int ret = 0;

        for (ChunkHolder chunkHolder : world.getChunkSource().chunkMap.visibleChunkMap.values()) {
            if (chunkHolder.getTickingChunk() != null) {
                ++ret;
            }
        }

        return ret; });
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

    public CraftWorld(ServerLevel world, ChunkGenerator gen, Environment env) {
        this.world = world;
        this.generator = gen;

        this.environment = env;
        // Paper start - per world spawn limits
        this.monsterSpawn = this.world.paperConfig.spawnLimitMonsters;
        this.animalSpawn = this.world.paperConfig.spawnLimitAnimals;
        this.waterAnimalSpawn = this.world.paperConfig.spawnLimitWaterAnimals;
        this.waterAmbientSpawn = this.world.paperConfig.spawnLimitWaterAmbient;
        this.ambientSpawn = this.world.paperConfig.spawnLimitAmbient;
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
            synchronized (world.getChunkSource().chunkMap.visibleChunkMap) {
                Long2ObjectLinkedOpenHashMap<ChunkHolder> chunks = world.getChunkSource().chunkMap.visibleChunkMap;
                return chunks.values().stream().map(ChunkHolder::getFullChunk).filter(Objects::nonNull).map(net.minecraft.world.level.chunk.LevelChunk::getBukkitChunk).toArray(Chunk[]::new);
            }
        }
        // Paper end
        Long2ObjectLinkedOpenHashMap<ChunkHolder> chunks = this.world.getChunkSource().chunkMap.visibleChunkMap;
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
        if (!this.isChunkLoaded(x, z)) {
            return false;
        }

        int px = x << 4;
        int pz = z << 4;

        // If there are more than 64 updates to a chunk at once, it will update all 'touched' sections within the chunk
        // And will include biome data if all sections have been 'touched'
        // This flags 65 blocks distributed across all the sections of the chunk, so that everything is sent, including biomes
        int height = this.getMaxHeight() / 16;
        for (int idx = 0; idx < 64; idx++) {
            this.world.sendBlockUpdated(new BlockPos(px + (idx / height), ((idx % height) * 16), pz), Blocks.AIR.defaultBlockState(), Blocks.STONE.defaultBlockState(), 3);
        }
        this.world.sendBlockUpdated(new BlockPos(px + 15, (height * 16) - 1, pz + 15), Blocks.AIR.defaultBlockState(), Blocks.STONE.defaultBlockState(), 3);

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
                file = world.getChunkSource().chunkMap.regionFileCache.getFile(chunkPos, false);
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
        this.world.addEntity(entity, SpawnReason.CUSTOM);
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
            arrow = net.minecraft.world.entity.EntityType.ARROW.create(world);
            ((net.minecraft.world.entity.projectile.Arrow) arrow).setType(CraftPotionUtil.fromBukkit(new PotionData(PotionType.WATER, false, false)));
        } else if (SpectralArrow.class.isAssignableFrom(clazz)) {
            arrow = net.minecraft.world.entity.EntityType.SPECTRAL_ARROW.create(world);
        } else if (Trident.class.isAssignableFrom(clazz)) {
            arrow = net.minecraft.world.entity.EntityType.TRIDENT.create(world);
        } else {
            arrow = net.minecraft.world.entity.EntityType.ARROW.create(world);
        }

        arrow.moveTo(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        arrow.shoot(velocity.getX(), velocity.getY(), velocity.getZ(), speed, spread);
        this.world.addFreshEntity(arrow);
        return (T) arrow.getBukkitEntity();
    }

    @Override
    public Entity spawnEntity(Location loc, EntityType entityType) {
        return this.spawn(loc, entityType.getEntityClass());
    }

    @Override
    public LightningStrike strikeLightning(Location loc) {
        LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(world);
        lightning.moveTo(loc.getX(), loc.getY(), loc.getZ());
        this.world.strikeLightning(lightning, LightningStrikeEvent.Cause.CUSTOM);
        return (LightningStrike) lightning.getBukkitEntity();
    }

    @Override
    public LightningStrike strikeLightningEffect(Location loc) {
        LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(world);
        lightning.moveTo(loc.getX(), loc.getY(), loc.getZ());
        lightning.setVisualOnly(true);
        this.world.strikeLightning(lightning, LightningStrikeEvent.Cause.CUSTOM);
        return (LightningStrike) lightning.getBukkitEntity();
    }

    @Override
    public boolean generateTree(Location loc, TreeType type) {
        BlockPos pos = new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        net.minecraft.world.level.levelgen.feature.ConfiguredFeature gen;
        switch (type) {
        case BIG_TREE:
            gen = Features.FANCY_OAK;
            break;
        case BIRCH:
            gen = Features.BIRCH;
            break;
        case REDWOOD:
            gen = Features.SPRUCE;
            break;
        case TALL_REDWOOD:
            gen = Features.PINE;
            break;
        case JUNGLE:
            gen = Features.MEGA_JUNGLE_TREE;
            break;
        case SMALL_JUNGLE:
            gen = Features.JUNGLE_TREE_NO_VINE;
            break;
        case COCOA_TREE:
            gen = Features.JUNGLE_TREE;
            break;
        case JUNGLE_BUSH:
            gen = Features.JUNGLE_BUSH;
            break;
        case RED_MUSHROOM:
            gen = Features.HUGE_RED_MUSHROOM;
            break;
        case BROWN_MUSHROOM:
            gen = Features.HUGE_BROWN_MUSHROOM;
            break;
        case SWAMP:
            gen = Features.SWAMP_OAK;
            break;
        case ACACIA:
            gen = Features.ACACIA;
            break;
        case DARK_OAK:
            gen = Features.DARK_OAK;
            break;
        case MEGA_REDWOOD:
            gen = Features.MEGA_PINE;
            break;
        case TALL_BIRCH:
            gen = Features.SUPER_BIRCH_BEES_0002;
            break;
        case CHORUS_PLANT:
            ((ChorusFlowerBlock) Blocks.CHORUS_FLOWER).generatePlant(world, pos, rand, 8);
            return true;
        case CRIMSON_FUNGUS:
            gen = Features.CRIMSON_FUNGI_PLANTED;
            break;
        case WARPED_FUNGUS:
            gen = Features.WARPED_FUNGI_PLANTED;
            break;
        case AZALEA:
            gen = Features.AZALEA_TREE;
            break;
        case TREE:
        default:
            gen = Features.OAK;
            break;
        }

        return gen.feature.place(new FeaturePlaceContext(this.world, this.world.getChunkSource().getGenerator(), CraftWorld.rand, pos, gen.config));
    }

    @Override
    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        world.captureTreeGeneration = true;
        boolean grownTree = this.generateTree(loc, type);
        world.captureTreeGeneration = false;
        if (grownTree) { // Copy block data to delegate
            for (BlockState blockstate : world.capturedBlockStates.values()) {
                blockstate.update(true);
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
        return !world.explode(source != null ? ((org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity) source).getHandle() : null, loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE).wasCanceled;
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
        return this.getBiome(x, 0, z);
    }

    @Override
    public Biome getBiome(int x, int y, int z) {
        return CraftBlock.biomeBaseToBiome(this.getHandle().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), this.world.getNoiseBiome(x >> 2, y >> 2, z >> 2));
    }

    @Override
    public void setBiome(int x, int z, Biome bio) {
        for (int y = 0; y < this.getMaxHeight(); y++) {
            this.setBiome(x, y, z, bio);
        }
    }

    @Override
    public void setBiome(int x, int y, int z, Biome bio) {
        Preconditions.checkArgument(bio != Biome.CUSTOM, "Cannot set the biome to %s", bio);
        net.minecraft.world.level.biome.Biome bb = CraftBlock.biomeToBiomeBase(this.getHandle().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), bio);
        BlockPos pos = new BlockPos(x, 0, z);
        if (this.world.hasChunkAt(pos)) {
            net.minecraft.world.level.chunk.LevelChunk chunk = this.world.getChunkAt(pos);

            if (chunk != null) {
                chunk.getBiomes().setBiome(x >> 2, y >> 2, z >> 2, bb);

                chunk.markUnsaved(); // SPIGOT-2890
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
    public List<Entity> getEntities() {
        List<Entity> list = new ArrayList<Entity>();

        this.world.getEntities().getAll().forEach((mcEnt) -> {
            Entity bukkitEntity = mcEnt.getBukkitEntity();

            // Assuming that bukkitEntity isn't null
            if (bukkitEntity != null && bukkitEntity.isValid()) {
                list.add(bukkitEntity);
            }
        });

        return list;
    }

    @Override
    public List<LivingEntity> getLivingEntities() {
        List<LivingEntity> list = new ArrayList<LivingEntity>();

        this.world.getEntities().getAll().forEach((mcEnt) -> {
            Entity bukkitEntity = mcEnt.getBukkitEntity();

            // Assuming that bukkitEntity isn't null
            if (bukkitEntity != null && bukkitEntity instanceof LivingEntity && bukkitEntity.isValid()) {
                list.add((LivingEntity) bukkitEntity);
            }
        });

        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Deprecated
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
        return (Collection<T>) this.getEntitiesByClasses(classes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> clazz) {
        Collection<T> list = new ArrayList<T>();

        this.world.getEntities().getAll().forEach((entity) -> {
            Entity bukkitEntity = ((net.minecraft.world.entity.Entity) entity).getBukkitEntity();

            if (bukkitEntity == null) {
                return;
            }

            Class<?> bukkitClass = bukkitEntity.getClass();

            if (clazz.isAssignableFrom(bukkitClass) && bukkitEntity.isValid()) {
                list.add((T) bukkitEntity);
            }
        });

        return list;
    }

    @Override
    public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
        Collection<Entity> list = new ArrayList<Entity>();

        this.world.getEntities().getAll().forEach((entity) -> {
            Entity bukkitEntity = ((net.minecraft.world.entity.Entity) entity).getBukkitEntity();

            if (bukkitEntity == null) {
                return;
            }

            Class<?> bukkitClass = bukkitEntity.getClass();

            for (Class<?> clazz : classes) {
                if (clazz.isAssignableFrom(bukkitClass)) {
                    if (bukkitEntity.isValid()) {
                        list.add(bukkitEntity);
                    }
                    break;
                }
            }
        });

        return list;
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
            Validate.isTrue(effect.getData() == null, "Wrong kind of data for this effect!");
        }

        int datavalue = data == null ? 0 : CraftEffect.getDataValue(effect, data);
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
    public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
        return this.spawn(location, clazz, null, SpawnReason.CUSTOM);
    }

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<T> function) throws IllegalArgumentException {
        return this.spawn(location, clazz, function, SpawnReason.CUSTOM);
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

        this.world.addEntity(entity, SpawnReason.CUSTOM);
        return (FallingBlock) entity.getBukkitEntity();
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, BlockData data) throws IllegalArgumentException {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(data, "Material cannot be null");

        FallingBlockEntity entity = new FallingBlockEntity(this.world, location.getX(), location.getY(), location.getZ(), ((CraftBlockData) data).getState());
        entity.time = 1;

        this.world.addEntity(entity, SpawnReason.CUSTOM);
        return (FallingBlock) entity.getBukkitEntity();
    }

    @SuppressWarnings("unchecked")
    public net.minecraft.world.entity.Entity createEntity(Location location, Class<? extends Entity> clazz) throws IllegalArgumentException {
        if (location == null || clazz == null) {
            throw new IllegalArgumentException("Location or entity class cannot be null");
        }

        net.minecraft.world.entity.Entity entity = null;

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float pitch = location.getPitch();
        float yaw = location.getYaw();

        // order is important for some of these
        if (Boat.class.isAssignableFrom(clazz)) {
            entity = new net.minecraft.world.entity.vehicle.Boat(this.world, x, y, z);
            entity.moveTo(x, y, z, yaw, pitch);
            // Paper start
        } else if (org.bukkit.entity.Item.class.isAssignableFrom(clazz)) {
            entity = new ItemEntity(world, x, y, z, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Item.byBlock(net.minecraft.world.level.block.Blocks.DIRT)));
            // Paper end
        } else if (FallingBlock.class.isAssignableFrom(clazz)) {
            entity = new FallingBlockEntity(this.world, x, y, z, this.world.getBlockState(new BlockPos(x, y, z)));
        } else if (Projectile.class.isAssignableFrom(clazz)) {
            if (Snowball.class.isAssignableFrom(clazz)) {
                entity = new net.minecraft.world.entity.projectile.Snowball(this.world, x, y, z);
            } else if (Egg.class.isAssignableFrom(clazz)) {
                entity = new ThrownEgg(this.world, x, y, z);
            } else if (AbstractArrow.class.isAssignableFrom(clazz)) {
                if (TippedArrow.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.ARROW.create(world);
                    ((net.minecraft.world.entity.projectile.Arrow) entity).setType(CraftPotionUtil.fromBukkit(new PotionData(PotionType.WATER, false, false)));
                } else if (SpectralArrow.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.SPECTRAL_ARROW.create(world);
                } else if (Trident.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.TRIDENT.create(world);
                } else {
                    entity = net.minecraft.world.entity.EntityType.ARROW.create(world);
                }
                entity.moveTo(x, y, z, 0, 0);
            } else if (ThrownExpBottle.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.EXPERIENCE_BOTTLE.create(world);
                entity.moveTo(x, y, z, 0, 0);
            } else if (EnderPearl.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.ENDER_PEARL.create(world);
                entity.moveTo(x, y, z, 0, 0);
            } else if (ThrownPotion.class.isAssignableFrom(clazz)) {
                if (LingeringPotion.class.isAssignableFrom(clazz)) {
                    entity = new net.minecraft.world.entity.projectile.ThrownPotion(this.world, x, y, z);
                    ((net.minecraft.world.entity.projectile.ThrownPotion) entity).setItem(CraftItemStack.asNMSCopy(new ItemStack(org.bukkit.Material.LINGERING_POTION, 1)));
                } else {
                    entity = new net.minecraft.world.entity.projectile.ThrownPotion(this.world, x, y, z);
                    ((net.minecraft.world.entity.projectile.ThrownPotion) entity).setItem(CraftItemStack.asNMSCopy(new ItemStack(org.bukkit.Material.SPLASH_POTION, 1)));
                }
            } else if (Fireball.class.isAssignableFrom(clazz)) {
                if (SmallFireball.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.SMALL_FIREBALL.create(world);
                } else if (WitherSkull.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.WITHER_SKULL.create(world);
                } else if (DragonFireball.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.DRAGON_FIREBALL.create(world);
                } else {
                    entity = net.minecraft.world.entity.EntityType.FIREBALL.create(world);
                }
                entity.moveTo(x, y, z, yaw, pitch);
                Vector direction = location.getDirection().multiply(10);
                ((AbstractHurtingProjectile) entity).setDirection(direction.getX(), direction.getY(), direction.getZ());
            } else if (ShulkerBullet.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.SHULKER_BULLET.create(world);
                entity.moveTo(x, y, z, yaw, pitch);
            } else if (LlamaSpit.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.LLAMA_SPIT.create(world);
                entity.moveTo(x, y, z, yaw, pitch);
            } else if (Firework.class.isAssignableFrom(clazz)) {
                entity = new FireworkRocketEntity(this.world, x, y, z, net.minecraft.world.item.ItemStack.EMPTY);
            }
        } else if (Minecart.class.isAssignableFrom(clazz)) {
            if (PoweredMinecart.class.isAssignableFrom(clazz)) {
                entity = new MinecartFurnace(this.world, x, y, z);
            } else if (StorageMinecart.class.isAssignableFrom(clazz)) {
                entity = new MinecartChest(this.world, x, y, z);
            } else if (ExplosiveMinecart.class.isAssignableFrom(clazz)) {
                entity = new MinecartTNT(this.world, x, y, z);
            } else if (HopperMinecart.class.isAssignableFrom(clazz)) {
                entity = new MinecartHopper(this.world, x, y, z);
            } else if (SpawnerMinecart.class.isAssignableFrom(clazz)) {
                entity = new MinecartSpawner(this.world, x, y, z);
            } else if (CommandMinecart.class.isAssignableFrom(clazz)) {
                entity = new MinecartCommandBlock(this.world, x, y, z);
            } else { // Default to rideable minecart for pre-rideable compatibility
                entity = new net.minecraft.world.entity.vehicle.Minecart(this.world, x, y, z);
            }
        } else if (EnderSignal.class.isAssignableFrom(clazz)) {
            entity = new EyeOfEnder(this.world, x, y, z);
        } else if (EnderCrystal.class.isAssignableFrom(clazz)) {
            entity = net.minecraft.world.entity.EntityType.END_CRYSTAL.create(world);
            entity.moveTo(x, y, z, 0, 0);
        } else if (LivingEntity.class.isAssignableFrom(clazz)) {
            if (Chicken.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.CHICKEN.create(world);
            } else if (Cow.class.isAssignableFrom(clazz)) {
                if (MushroomCow.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.MOOSHROOM.create(world);
                } else {
                    entity = net.minecraft.world.entity.EntityType.COW.create(world);
                }
            } else if (Golem.class.isAssignableFrom(clazz)) {
                if (Snowman.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.SNOW_GOLEM.create(world);
                } else if (IronGolem.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.IRON_GOLEM.create(world);
                } else if (Shulker.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.SHULKER.create(world);
                }
            } else if (Creeper.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.CREEPER.create(world);
            } else if (Ghast.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.GHAST.create(world);
            } else if (Pig.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.PIG.create(world);
            } else if (Player.class.isAssignableFrom(clazz)) {
                // need a net server handler for this one
            } else if (Sheep.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.SHEEP.create(world);
            } else if (AbstractHorse.class.isAssignableFrom(clazz)) {
                if (ChestedHorse.class.isAssignableFrom(clazz)) {
                    if (Donkey.class.isAssignableFrom(clazz)) {
                        entity = net.minecraft.world.entity.EntityType.DONKEY.create(world);
                    } else if (Mule.class.isAssignableFrom(clazz)) {
                        entity = net.minecraft.world.entity.EntityType.MULE.create(world);
                    } else if (Llama.class.isAssignableFrom(clazz)) {
                        if (TraderLlama.class.isAssignableFrom(clazz)) {
                            entity = net.minecraft.world.entity.EntityType.TRADER_LLAMA.create(world);
                        } else {
                            entity = net.minecraft.world.entity.EntityType.LLAMA.create(world);
                        }
                    }
                } else if (SkeletonHorse.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.SKELETON_HORSE.create(world);
                } else if (ZombieHorse.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.ZOMBIE_HORSE.create(world);
                } else {
                    entity = net.minecraft.world.entity.EntityType.HORSE.create(world);
                }
            } else if (AbstractSkeleton.class.isAssignableFrom(clazz)) {
                if (Stray.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.STRAY.create(world);
                } else if (WitherSkeleton.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.WITHER_SKELETON.create(world);
                } else if (Skeleton.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.SKELETON.create(world);
                }
            } else if (Slime.class.isAssignableFrom(clazz)) {
                if (MagmaCube.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.MAGMA_CUBE.create(world);
                } else {
                    entity = net.minecraft.world.entity.EntityType.SLIME.create(world);
                }
            } else if (Spider.class.isAssignableFrom(clazz)) {
                if (CaveSpider.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.CAVE_SPIDER.create(world);
                } else {
                    entity = net.minecraft.world.entity.EntityType.SPIDER.create(world);
                }
            } else if (Squid.class.isAssignableFrom(clazz)) {
                if (GlowSquid.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.GLOW_SQUID.create(world);
                } else {
                    entity = net.minecraft.world.entity.EntityType.SQUID.create(world);
                }
            } else if (Tameable.class.isAssignableFrom(clazz)) {
                if (Wolf.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.WOLF.create(world);
                } else if (Parrot.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.PARROT.create(world);
                } else if (Cat.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.CAT.create(world);
                }
            } else if (PigZombie.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.ZOMBIFIED_PIGLIN.create(world);
            } else if (Zombie.class.isAssignableFrom(clazz)) {
                if (Husk.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.HUSK.create(world);
                } else if (ZombieVillager.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.ZOMBIE_VILLAGER.create(world);
                } else if (Drowned.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.DROWNED.create(world);
                } else {
                    entity = new net.minecraft.world.entity.monster.Zombie(this.world);
                }
            } else if (Giant.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.GIANT.create(world);
            } else if (Silverfish.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.SILVERFISH.create(world);
            } else if (Enderman.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.ENDERMAN.create(world);
            } else if (Blaze.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.BLAZE.create(world);
            } else if (AbstractVillager.class.isAssignableFrom(clazz)) {
                if (Villager.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.VILLAGER.create(world);
                } else if (WanderingTrader.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.WANDERING_TRADER.create(world);
                }
            } else if (Witch.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.WITCH.create(world);
            } else if (Wither.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.WITHER.create(world);
            } else if (ComplexLivingEntity.class.isAssignableFrom(clazz)) {
                if (EnderDragon.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.ENDER_DRAGON.create(world);
                }
            } else if (Ambient.class.isAssignableFrom(clazz)) {
                if (Bat.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.BAT.create(world);
                }
            } else if (Rabbit.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.RABBIT.create(world);
            } else if (Endermite.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.ENDERMITE.create(world);
            } else if (Guardian.class.isAssignableFrom(clazz)) {
                if (ElderGuardian.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.ELDER_GUARDIAN.create(world);
                } else {
                    entity = net.minecraft.world.entity.EntityType.GUARDIAN.create(world);
                }
            } else if (ArmorStand.class.isAssignableFrom(clazz)) {
                entity = new net.minecraft.world.entity.decoration.ArmorStand(this.world, x, y, z);
            } else if (PolarBear.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.POLAR_BEAR.create(world);
            } else if (Vex.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.VEX.create(world);
            } else if (Illager.class.isAssignableFrom(clazz)) {
                if (Spellcaster.class.isAssignableFrom(clazz)) {
                    if (Evoker.class.isAssignableFrom(clazz)) {
                        entity = net.minecraft.world.entity.EntityType.EVOKER.create(world);
                    } else if (Illusioner.class.isAssignableFrom(clazz)) {
                        entity = net.minecraft.world.entity.EntityType.ILLUSIONER.create(world);
                    }
                } else if (Vindicator.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.VINDICATOR.create(world);
                } else if (Pillager.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.PILLAGER.create(world);
                }
            } else if (Turtle.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.TURTLE.create(world);
            } else if (Phantom.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.PHANTOM.create(world);
            } else if (Fish.class.isAssignableFrom(clazz)) {
                if (Cod.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.COD.create(world);
                } else if (PufferFish.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.PUFFERFISH.create(world);
                } else if (Salmon.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.SALMON.create(world);
                } else if (TropicalFish.class.isAssignableFrom(clazz)) {
                    entity = net.minecraft.world.entity.EntityType.TROPICAL_FISH.create(world);
                }
            } else if (Dolphin.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.DOLPHIN.create(world);
            } else if (Ocelot.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.OCELOT.create(world);
            } else if (Ravager.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.RAVAGER.create(world);
            } else if (Panda.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.PANDA.create(world);
            } else if (Fox.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.FOX.create(world);
            } else if (Bee.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.BEE.create(world);
            } else if (Hoglin.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.HOGLIN.create(world);
            } else if (Piglin.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.PIGLIN.create(world);
            } else if (PiglinBrute.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.PIGLIN_BRUTE.create(world);
            } else if (Strider.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.STRIDER.create(world);
            } else if (Zoglin.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.ZOGLIN.create(world);
            } else if (Axolotl.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.AXOLOTL.create(world);
            } else if (Goat.class.isAssignableFrom(clazz)) {
                entity = net.minecraft.world.entity.EntityType.GOAT.create(world);
            }

            if (entity != null) {
                entity.absMoveTo(x, y, z, yaw, pitch);
                entity.setYHeadRot(yaw); // SPIGOT-3587
            }
        } else if (Hanging.class.isAssignableFrom(clazz)) {
            BlockFace face = BlockFace.SELF;

            int width = 16; // 1 full block, also painting smallest size.
            int height = 16; // 1 full block, also painting smallest size.

            if (ItemFrame.class.isAssignableFrom(clazz)) {
                width = 12;
                height = 12;
            } else if (LeashHitch.class.isAssignableFrom(clazz)) {
                width = 9;
                height = 9;
            }

            // Paper start - In addition to d65a2576e40e58c8e446b330febe6799d13a604f do not check UP/DOWN for non item frames
            // BlockFace[] faces = new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN};
            BlockFace[] faces = (ItemFrame.class.isAssignableFrom(clazz))
                    ? new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN}
                    : new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
            // Paper end
            final BlockPos pos = new BlockPos(x, y, z);
            for (BlockFace dir : faces) {
                net.minecraft.world.level.block.state.BlockState nmsBlock = this.world.getBlockState(pos.relative(CraftBlock.blockFaceToNotch(dir)));
                if (nmsBlock.getMaterial().isSolid() || DiodeBlock.isDiode(nmsBlock)) {
                    boolean taken = false;
                    AABB bb = (ItemFrame.class.isAssignableFrom(clazz))
                            ? net.minecraft.world.entity.decoration.ItemFrame.calculateBoundingBox(null, pos, CraftBlock.blockFaceToNotch(dir).getOpposite(), width, height)
                            : HangingEntity.calculateBoundingBox(null, pos, CraftBlock.blockFaceToNotch(dir).getOpposite(), width, height);
                    List<net.minecraft.world.entity.Entity> list = (List<net.minecraft.world.entity.Entity>) this.world.getEntities(null, bb);
                    for (Iterator<net.minecraft.world.entity.Entity> it = list.iterator(); !taken && it.hasNext();) {
                        net.minecraft.world.entity.Entity e = it.next();
                        if (e instanceof HangingEntity) {
                            taken = true; // Hanging entities do not like hanging entities which intersect them.
                        }
                    }

                    if (!taken) {
                        face = dir;
                        break;
                    }
                }
            }

            if (LeashHitch.class.isAssignableFrom(clazz)) {
                entity = new LeashFenceKnotEntity(this.world, new BlockPos(x, y, z));
            } else {
                // No valid face found
                Preconditions.checkArgument(face != BlockFace.SELF, "Cannot spawn hanging entity for %s at %s (no free face)", clazz.getName(), location);

                Direction dir = CraftBlock.blockFaceToNotch(face).getOpposite();
                if (Painting.class.isAssignableFrom(clazz)) {
                    entity = new net.minecraft.world.entity.decoration.Painting(this.world, new BlockPos(x, y, z), dir);
                } else if (ItemFrame.class.isAssignableFrom(clazz)) {
                    if (GlowItemFrame.class.isAssignableFrom(clazz)) {
                        entity = new net.minecraft.world.entity.decoration.GlowItemFrame(this.world, new BlockPos(x, y, z), dir);
                    } else {
                        entity = new net.minecraft.world.entity.decoration.ItemFrame(this.world, new BlockPos(x, y, z), dir);
                    }
                }
            }

            if (entity != null && !((HangingEntity) entity).survives()) {
                throw new IllegalArgumentException("Cannot spawn hanging entity for " + clazz.getName() + " at " + location);
            }
        } else if (TNTPrimed.class.isAssignableFrom(clazz)) {
            entity = new PrimedTnt(this.world, x, y, z, null);
        } else if (ExperienceOrb.class.isAssignableFrom(clazz)) {
            entity = new net.minecraft.world.entity.ExperienceOrb(this.world, x, y, z, 0, org.bukkit.entity.ExperienceOrb.SpawnReason.CUSTOM, null, null); // Paper
        } else if (LightningStrike.class.isAssignableFrom(clazz)) {
            entity = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(world);
        } else if (AreaEffectCloud.class.isAssignableFrom(clazz)) {
            entity = new net.minecraft.world.entity.AreaEffectCloud(this.world, x, y, z);
            entity.moveTo(x, y, z, yaw, pitch); // Paper - Set area effect cloud Rotation
        } else if (EvokerFangs.class.isAssignableFrom(clazz)) {
            entity = new net.minecraft.world.entity.projectile.EvokerFangs(this.world, x, y, z, (float) Math.toRadians(yaw), 0, null);
        } else if (Marker.class.isAssignableFrom(clazz)) {
            entity = net.minecraft.world.entity.EntityType.MARKER.create(world);
            entity.setPos(x, y, z);
        }

        if (entity != null) {
            return entity;
        }

        throw new IllegalArgumentException("Cannot spawn an entity for " + clazz.getName());
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> T addEntity(net.minecraft.world.entity.Entity entity, SpawnReason reason) throws IllegalArgumentException {
        return this.addEntity(entity, reason, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> T addEntity(net.minecraft.world.entity.Entity entity, SpawnReason reason, Consumer<T> function) throws IllegalArgumentException {
        Preconditions.checkArgument(entity != null, "Cannot spawn null entity");

        if (entity instanceof Mob) {
            ((Mob) entity).finalizeSpawn(this.getHandle(), this.getHandle().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, (SpawnGroupData) null, null);
        }

        if (function != null) {
            function.accept((T) entity.getBukkitEntity());
        }

        this.world.addEntity(entity, reason);
        return (T) entity.getBukkitEntity();
    }

    public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<T> function, SpawnReason reason) throws IllegalArgumentException {
        net.minecraft.world.entity.Entity entity = this.createEntity(location, clazz);

        return this.addEntity(entity, reason, function);
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
            // TODO: doesn't work well if spawn changed.... // paper - resolved
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
        BlockPos nearest = getHandle().findNearestBiome(CraftBlock.biomeToBiomeBase(getHandle().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), biome), originPos, radius, step);
        return (nearest == null) ? null : new Location(this, nearest.getX(), nearest.getY(), nearest.getZ());
    }

    @Override
    public boolean isUltrawarm() {
        return getHandle().dimensionType().ultraWarm();
    }

    @Override
    public boolean isNatural() {
        return getHandle().dimensionType().natural();
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
    public boolean isPiglinSafe() {
        return getHandle().dimensionType().piglinSafe();
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
    public boolean hasRaids() {
        return getHandle().dimensionType().hasRaids();
    }

    @Override
    public boolean isFixedTime() {
        return getHandle().dimensionType().hasFixedTime();
    }

    @Override
    public Collection<org.bukkit.Material> getInfiniburn() {
        return com.google.common.collect.Sets.newHashSet(com.google.common.collect.Iterators.transform(getHandle().dimensionType().infiniburn().getValues().iterator(), CraftMagicNumbers::getMaterial));
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
        return org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey.fromMinecraft(world.dimension().location());
    }
    // Paper end

    // Spigot start
    @Override
    public int getViewDistance() {
        return getHandle().getChunkSource().chunkMap.getEffectiveViewDistance(); // Paper - no-tick view distance
    }
    // Spigot end

    // Paper start - per player view distance
    @Override
    public void setViewDistance(int viewDistance) {
        if (viewDistance < 2 || viewDistance > 32) {
            throw new IllegalArgumentException("View distance " + viewDistance + " is out of range of [2, 32]");
        }
        net.minecraft.server.level.ChunkMap chunkMap = getHandle().getChunkSource().chunkMap;
        if (viewDistance != chunkMap.getEffectiveViewDistance()) {
            chunkMap.setViewDistance(viewDistance);
        }
    }

    @Override
    public int getNoTickViewDistance() {
        return getHandle().getChunkSource().chunkMap.getEffectiveNoTickViewDistance();
    }

    @Override
    public void setNoTickViewDistance(int viewDistance) {
        if ((viewDistance < 2 || viewDistance > 32) && viewDistance != -1) {
            throw new IllegalArgumentException("View distance " + viewDistance + " is out of range of [2, 32]");
        }
        net.minecraft.server.level.ChunkMap chunkMap = getHandle().getChunkSource().chunkMap;
        if (viewDistance != chunkMap.getRawNoTickViewDistance()) {
            chunkMap.setNoTickViewDistance(viewDistance);
        }
    }
    // Paper end - per player view distance

    // Spigot start
    private final org.bukkit.World.Spigot spigot = new org.bukkit.World.Spigot()
    {

        @Override
        public LightningStrike strikeLightning(Location loc, boolean isSilent)
        {
            LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create( world );
            lightning.moveTo( loc.getX(), loc.getY(), loc.getZ() );
            lightning.isSilent = isSilent;
            CraftWorld.this.world.strikeLightning( lightning );
            return (LightningStrike) lightning.getBukkitEntity();
        }

        @Override
        public LightningStrike strikeLightningEffect(Location loc, boolean isSilent)
        {
            LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create( world );
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
}
