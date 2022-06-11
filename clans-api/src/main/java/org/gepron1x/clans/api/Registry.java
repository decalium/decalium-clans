package org.gepron1x.clans.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface Registry<K, V> extends Iterable<V> {

    @NotNull @Unmodifiable Set<V> values();
    Optional<V> value(K key);

    @NotNull @Unmodifiable  Map<K, V> asMap();

    @NotNull
    @Override
    default Iterator<V> iterator() {
        return values().iterator();
    }
}
