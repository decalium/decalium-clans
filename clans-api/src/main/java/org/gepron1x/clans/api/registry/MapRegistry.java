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
