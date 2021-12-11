package org.bukkit.craftbukkit.v1_18_R1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;

public class CraftEnderCrystal extends CraftEntity implements EnderCrystal {
    public CraftEnderCrystal(CraftServer server, EndCrystal entity) {
        super(server, entity);
    }

    @Override
    public boolean isShowingBottom() {
        return this.getHandle().showsBottom();
    }

    @Override
    public void setShowingBottom(boolean showing) {
        this.getHandle().setShowBottom(showing);
    }

    @Override
    public Location getBeamTarget() {
        BlockPos pos = this.getHandle().getBeamTarget();
        return pos == null ? null : new Location(getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void setBeamTarget(Location location) {
        if (location == null) {
            this.getHandle().setBeamTarget((BlockPos) null);
        } else if (location.getWorld() != getWorld()) {
            throw new IllegalArgumentException("Cannot set beam target location to different world");
        } else {
            this.getHandle().setBeamTarget(new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
    }

    @Override
    public EndCrystal getHandle() {
        return (EndCrystal) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderCrystal";
    }

    @Override
    public EntityType getType() {
        return EntityType.ENDER_CRYSTAL;
    }
}
