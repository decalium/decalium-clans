package org.gepron1x.clans.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Registry<K, V> {
    private final Function<V, K> keyFunction;
    private final Map<K, V> map;
    private Registry(Function<V, K> keyFunction, Map<K, V> mapImpl) {
        this.keyFunction = keyFunction;
        this.map = mapImpl;
    }
    public void register(V value) {
        K key = keyFunction.apply(value);
        Preconditions.checkArgument(!map.containsKey(key), " value with key is already registered");
        map.put(key, value);
    }
    @Nullable
    public V value(K key) {
        return map.get(key);
    }
    public void removeValue(V value) {
        Preconditions.checkArgument(map.containsValue(value), "this value want registered");
        map.remove(keyFunction.apply(value));
    }
    public void removeKey(K key) {
        Preconditions.checkArgument(map.containsKey(key), "no value with this key");
        map.remove(key);
    }
    public Collection<V> values() {
        return map.values();
    }
    public Collection<K> keys() {
        return map.keySet();
    }

    public void clear() {
        map.clear();
    }
    public boolean containsKey(K key) {
        return map.containsKey(key);


    }
    public Map<K, V> asMap() {
        return Collections.unmodifiableMap(map);

    }

    public boolean containsValue(V value) {
        return map.containsValue(value);
    }
    public static <K, V> Registry<K, V> create(Function<V, K> keyMapper, Collection<V> values) {
        Registry<K, V> registry = new Registry<>(keyMapper, new HashMap<>(values.size()));
        values.forEach(registry::register);
        return registry;
    }
    @SafeVarargs
    public static <K, V> Registry<K, V> create(Function<V, K> keyMapper, V... values) {
        Registry<K, V> registry = new Registry<>(keyMapper, new HashMap<>(values.length));
        for(V value : values) registry.register(value);
        return registry;
    }





}
