package org.bukkit.craftbukkit.v1_17_R1.block;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.persistence.PersistentDataContainer;

public class CraftBlockEntityState<T extends BlockEntity> extends CraftBlockState implements TileState {

    private final Class<T> tileEntityClass;
    private final T tileEntity;
    private final T snapshot;

    public CraftBlockEntityState(Block block, Class<T> tileEntityClass) {
        super(block);

        try {// Paper - show location on failure

        this.tileEntityClass = tileEntityClass;

        // get tile entity from block:
        CraftWorld world = (CraftWorld) this.getWorld();
        this.tileEntity = tileEntityClass.cast(world.getHandle().getBlockEntity(this.getPosition()));
        Preconditions.checkState(this.tileEntity != null, "Tile is null, asynchronous access? %s", block);

        // Paper start
        this.snapshotDisabled = DISABLE_SNAPSHOT;
        if (DISABLE_SNAPSHOT) {
            this.snapshot = this.tileEntity;
        } else {
            this.snapshot = this.createSnapshot(this.tileEntity);
        }
        // copy tile entity data:
        if(this.snapshot != null) {
            this.load(this.snapshot);
        }
        // Paper end
        // Paper start - show location on failure
        } catch (Throwable thr) {
            if (thr instanceof ThreadDeath) {
                throw (ThreadDeath)thr;
            }
            throw new RuntimeException("Failed to read BlockState at: world: " + block.getWorld().getName() + " location: (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ")", thr);
        }
        // Paper end
    }

    public final boolean snapshotDisabled; // Paper
    public static boolean DISABLE_SNAPSHOT = false; // Paper

    public CraftBlockEntityState(Material material, T tileEntity) {
        super(material);

        this.tileEntityClass = (Class<T>) tileEntity.getClass();
        this.tileEntity = tileEntity;
        // Paper start
        this.snapshotDisabled = DISABLE_SNAPSHOT;
        if (DISABLE_SNAPSHOT) {
            this.snapshot = this.tileEntity;
        } else {
            this.snapshot = this.createSnapshot(this.tileEntity);
        }
        // copy tile entity data:
        if(this.snapshot != null) {
            this.load(this.snapshot);
        }
        // Paper end
    }

    private T createSnapshot(T tileEntity) {
        if (tileEntity == null) {
            return null;
        }

        CompoundTag nbtTagCompound = tileEntity.save(new CompoundTag());
        T snapshot = (T) BlockEntity.loadStatic(getPosition(), getHandle(), nbtTagCompound);

        return snapshot;
    }

    // copies the TileEntity-specific data, retains the position
    private void copyData(T from, T to) {
        BlockPos pos = to.getBlockPos();
        CompoundTag nbtTagCompound = from.save(new CompoundTag());
        to.load(nbtTagCompound);
    }

    // gets the wrapped TileEntity
    public T getTileEntity() {
        return this.tileEntity;
    }

    // gets the cloned TileEntity which is used to store the captured data
    protected T getSnapshot() {
        return this.snapshot;
    }

    // gets the current TileEntity from the world at this position
    protected BlockEntity getTileEntityFromWorld() {
        requirePlaced();

        return ((CraftWorld) this.getWorld()).getHandle().getBlockEntity(this.getPosition());
    }

    // gets the NBT data of the TileEntity represented by this block state
    public CompoundTag getSnapshotNBT() {
        // update snapshot
        this.applyTo(this.snapshot);

        return this.snapshot.save(new CompoundTag());
    }

    // copies the data of the given tile entity to this block state
    protected void load(T tileEntity) {
        if (tileEntity != null && tileEntity != this.snapshot) {
            this.copyData(tileEntity, this.snapshot);
        }
    }

    // applies the TileEntity data of this block state to the given TileEntity
    protected void applyTo(T tileEntity) {
        if (tileEntity != null && tileEntity != this.snapshot) {
            this.copyData(this.snapshot, tileEntity);
        }
    }

    protected boolean isApplicable(BlockEntity tileEntity) {
        return this.tileEntityClass.isInstance(tileEntity);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result && this.isPlaced()) {
            BlockEntity tile = this.getTileEntityFromWorld();

            if (this.isApplicable(tile)) {
                this.applyTo(this.tileEntityClass.cast(tile));
                tile.setChanged();
            }
        }

        return result;
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return this.getSnapshot().persistentDataContainer;
    }
}
