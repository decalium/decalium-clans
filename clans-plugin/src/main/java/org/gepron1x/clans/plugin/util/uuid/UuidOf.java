/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
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
