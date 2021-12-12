package org.gepron1x.clans.plugin.util;

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


}
