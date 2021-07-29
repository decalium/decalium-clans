package org.bukkit.craftbukkit.v1_17_R1.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagCollection;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;

public abstract class CraftTag<N, B extends Keyed> implements Tag<B> {

    private final net.minecraft.tags.TagCollection<N> registry;
    private final ResourceLocation tag;
    //
    private net.minecraft.tags.Tag<N> handle;

    public CraftTag(TagCollection<N> registry, ResourceLocation tag) {
        this.registry = registry;
        this.tag = tag;
    }

    protected net.minecraft.tags.Tag<N> getHandle() {
        if (this.handle == null) {
            this.handle = this.registry.getTagOrEmpty(tag);
        }

        return this.handle;
    }

    @Override
    public NamespacedKey getKey() {
        return CraftNamespacedKey.fromMinecraft(tag);
    }
}
