package org.gepron1x.clans.plugin.util.uuid;

import space.arim.omnibus.util.UUIDUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public final class UuidOf implements UuidLike {

    private final byte[] bytes;
    private final int offset;

    public UuidOf(byte[] bytes, int offset) {
        this.bytes = bytes;
        this.offset = offset;
    }

    public UuidOf(byte[] bytes) {
        this(bytes, 0);
    }

    @Override
    public UUID uuid() {
        return UUIDUtil.fromByteArray(bytes, offset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UuidOf uuidOf = (UuidOf) o;
        return offset == uuidOf.offset && Arrays.equals(bytes, uuidOf.bytes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(offset);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }


}
