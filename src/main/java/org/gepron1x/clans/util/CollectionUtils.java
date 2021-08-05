package org.gepron1x.clans.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CollectionUtils {
    private CollectionUtils() { throw new UnsupportedOperationException("no"); }
    public static <K, V> Map<K, V> toMap(Collection<V> collection, Function<V, K> keyMapper) {
        Map<K, V> map = new HashMap<>(collection.size());
        for(V element : collection) {
            map.put(keyMapper.apply(element), element);
        }
        return map;
    }


}
