package org.gepron1x.clans.plugin.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public final class FancyCollections {

    private FancyCollections() {}

    public static <K, V> Map<K, V> asMap(@NotNull Function<V, K> keyMapper, @NotNull Collection<V> values) {
        if(values.isEmpty()) return Map.of();
        Map<K, V> map = new HashMap<>(values.size());
        for(V value : values) {
            map.put(keyMapper.apply(value), value);
        }
        return Map.copyOf(map);
    }

    @SafeVarargs
    public static <K, V> Map<K, V> asMap(@NotNull Function<V, K> keyMapper, @NotNull V@NotNull...values) {
        if(Objects.requireNonNull(values, "values").length == 0) return Map.of();
        return asMap(keyMapper, Arrays.asList(values));
    }


    public static <K, V> Map<K, V> createMap(@NotNull K k, @NotNull V v, @NotNull Object @NotNull... others) {
        int size = Objects.requireNonNull(others).length;
        Preconditions.checkArgument(size % 2 == 0, "length is odd");
        Map<K, V> map = new HashMap<>(1 + size / 2);
        map.put(k, v);
        for(int i = 0; i < size - 1; i++) {
            @SuppressWarnings("unchecked")
            K key = Objects.requireNonNull((K) others[i]);
            @SuppressWarnings("unchecked")
            V value = Objects.requireNonNull((V) others[i + 1]);
            map.put(key, value);
        }
        return Map.copyOf(map);
    }











}
