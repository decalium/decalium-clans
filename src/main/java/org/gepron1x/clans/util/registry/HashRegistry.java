package org.gepron1x.clans.util.registry;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class HashRegistry<K, V> implements Registry<K, V> {
    protected static final int DEFAULT_CAPACITY = 16;
    private final Function<V, K> keyMapper;
    protected final HashMap<K, V> backingMap;

    public HashRegistry(Function<V, K> keyMapper, int initialCapacity) {
        this.keyMapper = keyMapper;
        this.backingMap = new HashMap<>(initialCapacity);
    }
    public HashRegistry(Function<V, K> keyMapper) {
        this(keyMapper, DEFAULT_CAPACITY);
    }
    @Override
    public void add(@NotNull V value) {
        Preconditions.checkArgument(backingMap.containsValue(value), "value already in registry");
        backingMap.put(keyMapper.apply(value), value);
    }

    @Override
    public boolean remove(@NotNull K key) {
       return backingMap.remove(key) != null;
    }

    @Override
    public void addAll(@NotNull Collection<V> values) {
        for(V value : values) {
            add(value);
        }
    }

    @Override
    public @Nullable V get(@NotNull K key) {
        return backingMap.get(key);
    }

    @Override
    public @NotNull Collection<V> values() {
        return Collections.unmodifiableCollection(backingMap.values());
    }


    @Override
    public void clear() {
        backingMap.clear();
    }

    @Override
    public @NotNull Map<K, V> asMap() {
        return Map.copyOf(backingMap);
    }

    @Override
    public Function<V, K> keyMapper() {
        return keyMapper;
    }
    public static <K, V> HashRegistry<K, V> create(Function<V, K> keyMapper, Collection<V> values) {
        HashRegistry<K, V> registry = new HashRegistry<>(keyMapper, values.size());
        registry.addAll(values);
        return registry;
    }
    @SafeVarargs
    public static <K, V> HashRegistry<K, V> create(Function<V, K> keyMapper, V... values) {
        Component withFont = Component.text().content("Hello").font(Key.key("minecraft", "idk")).build();
        return create(keyMapper, Arrays.asList(values));

    }
    public static <K, V> HashRegistry<K, V> create(Function<V, K> keyMapper, Iterable<V> values) {
        if(values instanceof Collection) {
            return create(keyMapper, (Collection<V>) values);
        }
        HashRegistry<K, V> registry = new HashRegistry<>(keyMapper);
        for(V value : values) {
            registry.add(value);
        }
        return registry;

    }
}
