package org.bukkit.craftbukkit.v1_18_R1.entity;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Items; // Paper
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material; // Paper
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Minecart;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public abstract class CraftMinecart extends CraftVehicle implements Minecart {
    public CraftMinecart(CraftServer server, AbstractMinecart entity) {
        super(server, entity);
    }

    @Override
    public void setDamage(double damage) {
        this.getHandle().setDamage((float) damage);
    }

    @Override
    public double getDamage() {
        return this.getHandle().getDamage();
    }

    @Override
    public double getMaxSpeed() {
        return this.getHandle().maxSpeed;
    }

    @Override
    public void setMaxSpeed(double speed) {
        if (speed >= 0D) {
            this.getHandle().maxSpeed = speed;
        }
    }

    @Override
    public boolean isSlowWhenEmpty() {
        return this.getHandle().slowWhenEmpty;
    }

    @Override
    public void setSlowWhenEmpty(boolean slow) {
        this.getHandle().slowWhenEmpty = slow;
    }

    @Override
    public Vector getFlyingVelocityMod() {
        return this.getHandle().getFlyingVelocityMod();
    }

    @Override
    public void setFlyingVelocityMod(Vector flying) {
        this.getHandle().setFlyingVelocityMod(flying);
    }

    @Override
    public Vector getDerailedVelocityMod() {
        return this.getHandle().getDerailedVelocityMod();
    }

    @Override
    public void setDerailedVelocityMod(Vector derailed) {
        this.getHandle().setDerailedVelocityMod(derailed);
    }

    // Paper start
    @Override
    public Material getMinecartMaterial() {
         net.minecraft.world.item.Item minecartItem = switch (getHandle().getMinecartType()) {
            case CHEST -> Items.CHEST_MINECART;
            case FURNACE ->  Items.FURNACE_MINECART;
            case TNT ->  Items.TNT_MINECART;
            case HOPPER ->  Items.HOPPER_MINECART;
            case COMMAND_BLOCK ->  Items.COMMAND_BLOCK_MINECART;
            case RIDEABLE, SPAWNER ->  Items.MINECART;
        };

        return CraftMagicNumbers.getMaterial(minecartItem);
    }
    // Paper end

    @Override
    public AbstractMinecart getHandle() {
        return (AbstractMinecart) entity;
    }

    @Override
    public void setDisplayBlock(MaterialData material) {
        if (material != null) {
            BlockState block = CraftMagicNumbers.getBlock(material);
            this.getHandle().setDisplayBlockState(block);
        } else {
            // Set block to air (default) and set the flag to not have a display block.
            this.getHandle().setDisplayBlockState(Blocks.AIR.defaultBlockState());
            this.getHandle().setCustomDisplay(false);
        }
    }

    @Override
    public void setDisplayBlockData(BlockData blockData) {
        if (blockData != null) {
            BlockState block = ((CraftBlockData) blockData).getState();
            this.getHandle().setDisplayBlockState(block);
        } else {
            // Set block to air (default) and set the flag to not have a display block.
            this.getHandle().setDisplayBlockState(Blocks.AIR.defaultBlockState());
            this.getHandle().setCustomDisplay(false);
        }
    }

    @Override
    public MaterialData getDisplayBlock() {
        BlockState blockData = this.getHandle().getDisplayBlockState();
        return CraftMagicNumbers.getMaterial(blockData);
    }

    @Override
    public BlockData getDisplayBlockData() {
        BlockState blockData = this.getHandle().getDisplayBlockState();
        return CraftBlockData.fromData(blockData);
    }

    @Override
    public void setDisplayBlockOffset(int offset) {
        this.getHandle().setDisplayOffset(offset);
    }

    @Override
    public int getDisplayBlockOffset() {
        return this.getHandle().getDisplayOffset();
    }
}
