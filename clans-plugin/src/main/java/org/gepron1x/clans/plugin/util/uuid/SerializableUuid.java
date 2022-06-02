package org.gepron1x.clans.plugin.util.uuid;

import space.arim.omnibus.util.UUIDUtil;

import java.util.Objects;
import java.util.UUID;

public final class SerializableUuid implements UuidLike {
    private final UUID uuid;

    public SerializableUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public byte[] serialize() {
        return UUIDUtil.toByteArray(uuid);
    }

    public void write(byte[] byteArray, int offset) {
        UUIDUtil.toByteArray(this.uuid, byteArray, offset);
    }


    @Override
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializableUuid that = (SerializableUuid) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
