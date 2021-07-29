package org.bukkit.craftbukkit.v1_17_R1.block;

import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.EnchantingTable;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;

public class CraftEnchantingTable extends CraftBlockEntityState<EnchantmentTableBlockEntity> implements EnchantingTable {

    public CraftEnchantingTable(final Block block) {
        super(block, EnchantmentTableBlockEntity.class);
    }

    public CraftEnchantingTable(final Material material, final EnchantmentTableBlockEntity te) {
        super(material, te);
    }

    // Paper start
    @Override
    public net.kyori.adventure.text.Component customName() {
        final EnchantmentTableBlockEntity be = this.getSnapshot();
        return be.hasCustomName() ? io.papermc.paper.adventure.PaperAdventure.asAdventure(be.getCustomName()) : null;
    }

    @Override
    public void customName(final net.kyori.adventure.text.Component customName) {
        this.getSnapshot().setCustomName(customName != null ? io.papermc.paper.adventure.PaperAdventure.asVanilla(customName) : null);
    }
    // Paper end

    @Override
    public String getCustomName() {
        EnchantmentTableBlockEntity enchant = this.getSnapshot();
        return enchant.hasCustomName() ? CraftChatMessage.fromComponent(enchant.getCustomName()) : null;
    }

    @Override
    public void setCustomName(String name) {
        this.getSnapshot().setCustomName(CraftChatMessage.fromStringOrNull(name));
    }

    @Override
    public void applyTo(EnchantmentTableBlockEntity enchantingTable) {
        super.applyTo(enchantingTable);

        if (!this.getSnapshot().hasCustomName()) {
            enchantingTable.setCustomName(null);
        }
    }
}
