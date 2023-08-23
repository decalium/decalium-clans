package org.gepron1x.clans.plugin.util;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public final class MapRegistry<K, V> implements Registry<K, V> {

	private final Map<K, V> map;

	public MapRegistry(Map<K, V> map) {

		this.map = map;
	}
	@Override
	public @NotNull @Unmodifiable Collection<V> values() {
		return Collections.unmodifiableCollection(map.values());
	}

	@Override
	public Optional<V> value(K key) {
		return Optional.ofNullable(map.get(key));
	}

	@Override
	public @NotNull @Unmodifiable Map<K, V> asMap() {
		return Collections.unmodifiableMap(map);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MapRegistry<?, ?> that = (MapRegistry<?, ?>) o;
		return Objects.equals(map, that.map);
	}

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("map", map)
				.toString();
	}
}
