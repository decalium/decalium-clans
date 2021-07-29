package org.bukkit.craftbukkit.v1_17_R1.block;

import com.google.common.base.Preconditions;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CraftBlockState implements BlockState {
    protected final CraftWorld world;
    private final BlockPos position;
    protected net.minecraft.world.level.block.state.BlockState data;
    protected int flag;

    public CraftBlockState(final Block block) {
        this.world = (CraftWorld) block.getWorld();
        this.position = ((CraftBlock) block).getPosition();
        this.data = ((CraftBlock) block).getNMS();
        this.flag = 3;
    }

    public CraftBlockState(final Block block, int flag) {
        this(block);
        this.flag = flag;
    }

    public CraftBlockState(Material material) {
        this.world = null;
        this.data = CraftMagicNumbers.getBlock(material).defaultBlockState();
        this.position = BlockPos.ZERO;
    }

    public static CraftBlockState getBlockState(LevelAccessor world, net.minecraft.core.BlockPos pos) {
        return new CraftBlockState(CraftBlock.at(world, pos));
    }

    public static CraftBlockState getBlockState(LevelAccessor world, net.minecraft.core.BlockPos pos, int flag) {
        return new CraftBlockState(CraftBlock.at(world, pos), flag);
    }

    @Override
    public World getWorld() {
        this.requirePlaced();
        return this.world;
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
        this.requirePlaced();
        return this.world.getChunkAt(this.getX() >> 4, this.getZ() >> 4);
    }

    public void setData(net.minecraft.world.level.block.state.BlockState data) {
        this.data = data;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public net.minecraft.world.level.block.state.BlockState getHandle() {
        return this.data;
    }

    @Override
    public BlockData getBlockData() {
        return CraftBlockData.fromData(data);
    }

    @Override
    public void setBlockData(BlockData data) {
        Preconditions.checkArgument(data != null, "BlockData cannot be null");
        this.data = ((CraftBlockData) data).getState();
    }

    @Override
    public void setData(final MaterialData data) {
        Material mat = CraftMagicNumbers.getMaterial(this.data).getItemType();

        if ((mat == null) || (mat.getData() == null)) {
            this.data = CraftMagicNumbers.getBlock(data);
        } else {
            if ((data.getClass() == mat.getData()) || (data.getClass() == MaterialData.class)) {
                this.data = CraftMagicNumbers.getBlock(data);
            } else {
                throw new IllegalArgumentException("Provided data is not of type "
                        + mat.getData().getName() + ", found " + data.getClass().getName());
            }
        }
    }

    @Override
    public MaterialData getData() {
        return CraftMagicNumbers.getMaterial(data);
    }

    @Override
    public void setType(final Material type) {
        Preconditions.checkArgument(type != null, "Material cannot be null");
        Preconditions.checkArgument(type.isBlock(), "Material must be a block!");

        if (this.getType() != type) {
            this.data = CraftMagicNumbers.getBlock(type).defaultBlockState();
        }
    }

    @Override
    public Material getType() {
        return this.data.getBukkitMaterial(); // Paper - optimise getType calls
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return this.flag;
    }

    @Override
    public byte getLightLevel() {
        return this.getBlock().getLightLevel();
    }

    @Override
    public CraftBlock getBlock() {
        this.requirePlaced();
        return CraftBlock.at(this.world.getHandle(), position);
    }

    @Override
    public boolean update() {
        return this.update(false);
    }

    @Override
    public boolean update(boolean force) {
        return this.update(force, true);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        if (!this.isPlaced()) {
            return true;
        }
        CraftBlock block = this.getBlock();

        if (block.getType() != this.getType()) {
            if (!force) {
                return false;
            }
        }

        net.minecraft.world.level.block.state.BlockState newBlock = this.data;
        block.setTypeAndData(newBlock, applyPhysics);
        this.world.getHandle().sendBlockUpdated(
                position,
                block.getNMS(),
                newBlock,
                3
        );

        // Update levers etc
        if (false && applyPhysics && this.getData() instanceof Attachable) { // Call does not map to new API
            this.world.getHandle().updateNeighborsAt(this.position.relative(CraftBlock.blockFaceToNotch(((Attachable) this.getData()).getAttachedFace())), newBlock.getBlock());
        }

        return true;
    }

    @Override
    public byte getRawData() {
        return CraftMagicNumbers.toLegacyData(data);
    }

    @Override
    public Location getLocation() {
        return new Location(this.world, this.getX(), this.getY(), this.getZ());
    }

    @Override
    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(world);
            loc.setX(this.getX());
            loc.setY(this.getY());
            loc.setZ(this.getZ());
            loc.setYaw(0);
            loc.setPitch(0);
        }

        return loc;
    }

    @Override
    public void setRawData(byte data) {
        this.data = CraftMagicNumbers.getBlock(this.getType(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CraftBlockState other = (CraftBlockState) obj;
        if (this.world != other.world && (this.world == null || !this.world.equals(other.world))) {
            return false;
        }
        if (this.position != other.position && (this.position == null || !this.position.equals(other.position))) {
            return false;
        }
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 73 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 73 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.requirePlaced();
        this.world.getBlockMetadata().setMetadata(this.getBlock(), metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        this.requirePlaced();
        return this.world.getBlockMetadata().getMetadata(this.getBlock(), metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        this.requirePlaced();
        return this.world.getBlockMetadata().hasMetadata(this.getBlock(), metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.requirePlaced();
        this.world.getBlockMetadata().removeMetadata(this.getBlock(), metadataKey, owningPlugin);
    }

    @Override
    public boolean isPlaced() {
        return this.world != null;
    }

    protected void requirePlaced() {
        if (!this.isPlaced()) {
            throw new IllegalStateException("The blockState must be placed to call this method");
        }
    }
}
