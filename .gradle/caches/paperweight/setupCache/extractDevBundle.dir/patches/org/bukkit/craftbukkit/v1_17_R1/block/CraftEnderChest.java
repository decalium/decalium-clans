package org.bukkit.craftbukkit.v1_17_R1.block;

import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;

public class CraftEnderChest extends CraftBlockEntityState<EnderChestBlockEntity> implements EnderChest {

    public CraftEnderChest(final Block block) {
        super(block, EnderChestBlockEntity.class);
    }

    public CraftEnderChest(final Material material, final EnderChestBlockEntity te) {
        super(material, te);
    }

    // Paper start - More Lidded Block API
    @Override
    public void open() {
        requirePlaced();
        if (!getTileEntity().openersCounter.opened) {
            net.minecraft.world.level.Level world = getTileEntity().getLevel();
            world.blockEvent(getTileEntity().getBlockPos(), getTileEntity().getBlockState().getBlock(), 1, getTileEntity().openersCounter.getOpenerCount() + 1);
            world.playSound(null, getPosition(), net.minecraft.sounds.SoundEvents.ENDER_CHEST_OPEN, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
        getTileEntity().openersCounter.opened = true;
    }

    @Override
    public void close() {
        requirePlaced();
        if (getTileEntity().openersCounter.opened) {
            net.minecraft.world.level.Level world = getTileEntity().getLevel();
            world.blockEvent(getTileEntity().getBlockPos(), getTileEntity().getBlockState().getBlock(), 1, 0);
            world.playSound(null, getPosition(), net.minecraft.sounds.SoundEvents.ENDER_CHEST_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
        getTileEntity().openersCounter.opened = false;
    }

    @Override
    public boolean isOpen() {
        return getTileEntity().openersCounter.opened;
    }
    // Paper end - More Lidded Block API
}
