package org.gepron1x.clans.plugin.util.pdc;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.UUIDUtil;

import java.util.UUID;

public final class UuidDataType implements PersistentDataType<byte[], UUID> {
    public static final UuidDataType UUID = new UuidDataType();

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<UUID> getComplexType() {
        return UUID.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull UUID uuid, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return UUIDUtil.toByteArray(uuid);
    }

    @Override
    public @NotNull UUID fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return UUIDUtil.fromByteArray(bytes);
    }
}
