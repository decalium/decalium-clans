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
package org.gepron1x.clans.api.edition;

import org.gepron1x.clans.api.registry.Registry;

import java.util.Collection;
import java.util.function.Consumer;

public interface RegistryEdition<K, V, E extends Edition<V>, T extends Registry<K, V>> extends Edition<T> {


    RegistryEdition<K, V, E, T> add(V value);
    RegistryEdition<K, V, E, T> add(Collection<V> values);

    RegistryEdition<K, V, E, T> remove(K key);

    RegistryEdition<K, V, E, T> edit(K key, Consumer<E> consumer);




}
