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
        if(values.isEmpty()) return Map.of();
        return Map.copyOf(create(HashMap::new));
    }

    public <M extends Map<K, V>> M create(IntFunction<M> mapFactory) {
        M map = mapFactory.apply(values.size());
        for(V value : values) {
            map.put(keyMapper.apply(value), value);
        }
        return map;
    }


}
