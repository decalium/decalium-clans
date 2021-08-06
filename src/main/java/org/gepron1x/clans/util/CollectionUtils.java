package org.gepron1x.clans.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class CollectionUtils {
    private CollectionUtils() { throw new UnsupportedOperationException("no"); }
    public static <K, V, M extends Map<K, V>> M toMap(IntFunction<M> mapFactory, Function<V, K> keyMapper, Collection<V> collection) {
        M map = mapFactory.apply(collection.size());
        for(V element : collection) {
            map.put(keyMapper.apply(element), element);
        }
        return map;
    }
    public static <K, V> HashMap<K, V> toMap(Function<V, K> keyMapper, Collection<V> collection) {
        return toMap(HashMap::new, keyMapper, collection);
    }
    @SafeVarargs
    public static <K, V> HashMap<K, V> toMap(Function<V, K> keyMapper, V... values) {
        return toMap(keyMapper, Arrays.asList(values));
    }


}
