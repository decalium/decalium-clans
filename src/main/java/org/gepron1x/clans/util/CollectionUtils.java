package org.gepron1x.clans.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class CollectionUtils {
    private CollectionUtils() {
        throw new UnsupportedOperationException("no");
    }

    public static <K, V, M extends Map<K, V>> M toMap(IntFunction<M> mapFactory,
                                                      Function<V, K> keyMapper,
                                                      Collection<V> collection) {
        M map = mapFactory.apply(collection.size());
        for (V element : collection) {
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

    @SuppressWarnings("unchecked")
    public static <K, V, M extends Map<K, V>> M createMap(IntFunction<M> mapFactory, K k, V v, Object... input) {
        Objects.requireNonNull(mapFactory, "mapFactory");
        Preconditions.checkArgument(input.length % 2 == 0, "length is odd");
        M map = mapFactory.apply(input.length / 2 + 2);
        map.put(k, v);

        for (int i = 0; i < input.length - 1; i++) {
            K key = Objects.requireNonNull((K) input[i]);
            V value = Objects.requireNonNull((V) input[i + 1]);
            map.put(key, value);
        }
        return map;
    }

    public static <K, V> HashMap<K, V> createMap(K k, V v, Object... input) {
        return createMap(HashMap::new, k, v, input);
    }

    public static <T> Collector<T, ArrayList<T>, T[]> toArray(T[] array) {

        return Collector.of(
                ArrayList::new,
                ArrayList::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                list -> list.toArray(array));
    }

    public static <T> Collector<T, ArrayList<T>, T[]> toArray(IntFunction<T[]> arrayFactory) {
        return toArray(arrayFactory.apply(0));
    }


}
