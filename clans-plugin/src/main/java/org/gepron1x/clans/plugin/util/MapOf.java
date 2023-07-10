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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

public record MapOf<K, V>(Function<V, K> keyMapper, Collection<V> values) {

	@SafeVarargs
	public MapOf(Function<V, K> keyMapper, V... values) {
		this(keyMapper, Arrays.asList(values));
	}

	public Map<K, V> create() {
		if (values.isEmpty()) return Map.of();
		return Map.copyOf(create(HashMap::new));
	}

	public <M extends Map<K, V>> M create(IntFunction<M> mapFactory) {
		M map = mapFactory.apply(values.size());
		for (V value : values) {
			map.put(keyMapper.apply(value), value);
		}
		return map;
	}


}
