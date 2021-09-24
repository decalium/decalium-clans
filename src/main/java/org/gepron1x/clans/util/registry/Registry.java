package org.gepron1x.clans.util.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface Registry<K, V> {
    void add(@NotNull V value);
    boolean remove(@NotNull K key);
    default boolean removeValue(@NotNull V value) {
        return remove(keyMapper().apply(value));
    }
    void addAll(@NotNull Collection<V> values);
    @Nullable V get(@NotNull K key);

    default V getOr(@NotNull K key, V fallback) {
        V val = get(key);
        return val == null ? fallback : val;
    }
    @NotNull
    Collection<V> values();
    default K getKey(@NotNull V value) {
        return keyMapper().apply(value);
    }
    void clear();
    @NotNull Map<K, V> asMap();

    Function<V, K> keyMapper();

}
