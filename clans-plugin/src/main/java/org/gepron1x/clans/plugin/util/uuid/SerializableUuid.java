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
