package org.gepron1x.clans.events;

import org.gepron1x.clans.util.Events;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DefaultProperty<T, V> implements Property<T, V> {

    private final String name;
    private final Class<T> targetType;
    private final Class<V> valueType;
    private final Function<T, V> getter;
    private final BiConsumer<T, V> setter;

    public DefaultProperty(String name, Class<T> targetType, Class<V> valueType, Function<T, V> getter, BiConsumer<T, V> setter) {
        this.name = name;

        this.targetType = targetType;
        this.valueType = valueType;
        this.getter = getter;
        this.setter = setter;
    }
    @Override
    public @NotNull Class<T> getTargetType() {
        return targetType;
    }

    @Override
    public @NotNull Class<V> getValueType() {
        return valueType;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void set(@NotNull T target, V value) {
        PropertyUpdateEvent event = Events.callEvent(new PropertyUpdateEvent(this, target, value));
        if(event.isCancelled()) return;
        setter.accept(target, valueType.cast(event.getValue()));
    }

    @Override
    public V get(T value) {
        return getter.apply(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultProperty<?, ?> that = (DefaultProperty<?, ?>) o;
        return name.equals(that.name) && targetType.equals(that.targetType) && valueType.equals(that.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, targetType, valueType);
    }
}
