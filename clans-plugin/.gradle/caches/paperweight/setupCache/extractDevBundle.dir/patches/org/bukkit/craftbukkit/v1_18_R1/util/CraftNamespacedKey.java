package org.bukkit.craftbukkit.v1_18_R1.util;

import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;

public final class CraftNamespacedKey {

    public CraftNamespacedKey() {
    }

    public static NamespacedKey fromStringOrNull(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        ResourceLocation minecraft = ResourceLocation.tryParse(string);
        return (minecraft == null || minecraft.getPath().isEmpty()) ? null : CraftNamespacedKey.fromMinecraft(minecraft); // Paper - Bukkit's parser does not match Vanilla for empty paths
    }

    public static NamespacedKey fromString(String string) {
        return CraftNamespacedKey.fromMinecraft(new ResourceLocation(string));
    }

    public static NamespacedKey fromMinecraft(ResourceLocation minecraft) {
        return new NamespacedKey(minecraft.getNamespace(), minecraft.getPath());
    }

    public static ResourceLocation toMinecraft(NamespacedKey key) {
        return new ResourceLocation(key.getNamespace(), key.getKey());
    }
}
