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
package org.gepron1x.clans.api.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class MapRegistry<K, V> implements Registry<K, V> {

    private final Map<K, V> map;

    public MapRegistry(Map<K, V> map) {
        this.map = map;
    }


    @Override
    public @NotNull @Unmodifiable Collection<V> values() {
        return this.map.values();
    }

    @Override
    public Optional<V> value(K key) {
        return Optional.of(map.get(key));
    }

    @Override
    public @NotNull @Unmodifiable Map<K, V> asMap() {
        return map;
    }
}
