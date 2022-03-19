package org.gepron1x.clans.plugin.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record MapOf<K, V>(Function<V, K> keyMapper, Collection<V> values) {

    @SafeVarargs
    public MapOf(Function<V, K> keyMapper, V... values) {
        this(keyMapper, Arrays.asList(values));
    }

    public Map<K, V> create() {
        if(values.isEmpty()) return Map.of();
        Map<K, V> map = new HashMap<>(values.size());
        for(V value : values) {
            map.put(keyMapper.apply(value), value);
        }
        return Map.copyOf(map);
    }
}
