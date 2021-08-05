package org.gepron1x.clans.events;

import org.jetbrains.annotations.NotNull;

public interface Property<T, V> {
    @NotNull Class<T> getTargetType();
    @NotNull Class<V> getValueType();

    @NotNull String getName();
    void set(@NotNull T target, V value);
    V get(T value);

}
