package org.gepron1x.clans.util;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public final class TranslatableComponents {
    private TranslatableComponents() {throw new UnsupportedOperationException("well, you tried"); }

    public static TranslatableComponent entityType(EntityType type) {
        return Component.translatable(Bukkit.getUnsafe().getTranslationKey(type));
    }

    public static TranslatableComponent material(Material material) {
        return Component.translatable(Bukkit.getUnsafe().getTranslationKey(material));
    }
    public static TranslatableComponent itemStack(ItemStack itemStack) {
        return Component.translatable(Bukkit.getUnsafe().getTranslationKey(itemStack));
    }
    public static TranslatableComponent block(Block block) {
        return Component.translatable(Bukkit.getUnsafe().getTranslationKey(block));
    }
}
