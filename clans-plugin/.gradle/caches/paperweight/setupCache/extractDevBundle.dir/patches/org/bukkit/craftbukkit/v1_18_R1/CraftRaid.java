package org.bukkit.craftbukkit.v1_18_R1;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.entity.Raider;

public final class CraftRaid implements Raid {

    private final net.minecraft.world.entity.raid.Raid handle;

    public CraftRaid(net.minecraft.world.entity.raid.Raid handle) {
        this.handle = handle;
    }

    @Override
    public boolean isStarted() {
        return this.handle.isStarted();
    }

    @Override
    public long getActiveTicks() {
        return handle.ticksActive;
    }

    @Override
    public int getBadOmenLevel() {
        return handle.badOmenLevel;
    }

    @Override
    public void setBadOmenLevel(int badOmenLevel) {
        int max = this.handle.getMaxBadOmenLevel();
        Preconditions.checkArgument(0 <= badOmenLevel && badOmenLevel <= max, "Bad Omen level must be between 0 and %s", max);
        handle.badOmenLevel = badOmenLevel;
    }

    @Override
    public Location getLocation() {
        BlockPos pos = this.handle.getCenter();
        Level world = this.handle.getLevel();
        return new Location(world.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public RaidStatus getStatus() {
        if (this.handle.isStopped()) {
            return RaidStatus.STOPPED;
        } else if (this.handle.isVictory()) {
            return RaidStatus.VICTORY;
        } else if (this.handle.isLoss()) {
            return RaidStatus.LOSS;
        } else {
            return RaidStatus.ONGOING;
        }
    }

    @Override
    public int getSpawnedGroups() {
        return this.handle.getGroupsSpawned();
    }

    @Override
    public int getTotalGroups() {
        return handle.numGroups + (handle.badOmenLevel > 1 ? 1 : 0);
    }

    @Override
    public int getTotalWaves() {
        return handle.numGroups;
    }

    @Override
    public float getTotalHealth() {
        return this.handle.getHealthOfLivingRaiders();
    }

    @Override
    public Set<UUID> getHeroes() {
        return Collections.unmodifiableSet(handle.heroesOfTheVillage);
    }

    @Override
    public List<Raider> getRaiders() {
        return this.handle.getRaiders().stream().map(new Function<net.minecraft.world.entity.raid.Raider, Raider>() {
            @Override
            public Raider apply(net.minecraft.world.entity.raid.Raider entityRaider) {
                return (Raider) entityRaider.getBukkitEntity();
            }
        }).collect(ImmutableList.toImmutableList());
    }
}
