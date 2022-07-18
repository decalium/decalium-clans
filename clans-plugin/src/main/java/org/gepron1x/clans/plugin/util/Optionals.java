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
package org.gepron1x.clans.plugin.util;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class Optionals {
    private Optionals() {
        throw new UnsupportedOperationException();
    }

    public static OptionalInt ofNullable(@Nullable Integer value) {
        return value == null ? OptionalInt.empty() : OptionalInt.of(value);

    }

    public static OptionalLong ofNullable(@Nullable Long value) {
        return value == null ? OptionalLong.empty() : OptionalLong.of(value);
    }

    public static OptionalDouble ofNullable(@Nullable Double value) {
        return value == null ? OptionalDouble.empty() : OptionalDouble.of(value);
    }

    public static <T> Optional<T> ofIterator(Iterator<T> iterator) {
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    public static <T> Optional<T> ofIterable(Iterable<T> iterable) {
        return ofIterator(iterable.iterator());
    }

}
