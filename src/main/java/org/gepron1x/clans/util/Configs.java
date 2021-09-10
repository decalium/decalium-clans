package org.gepron1x.clans.util;

import org.jetbrains.annotations.Nullable;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.FlexibleTypeFunction;
import space.arim.dazzleconf.serialiser.FlexibleTypeMapEntryFunction;

import java.util.List;
import java.util.Map;

public final class Configs {
    private Configs() {throw new UnsupportedOperationException("no instances"); }
    public static <K, V> FlexibleTypeMapEntryFunction<? extends K, ? extends V> entryProcessor(FlexibleTypeFunction<? extends K> keyProcessor, FlexibleTypeFunction<? extends V> valueProcessor) {
        return (key, value) -> Map.entry(keyProcessor.getResult(key), valueProcessor.getResult(value));
    }

    public static FlexibleTypeMapEntryFunction<? extends String, ? extends FlexibleType> section() {
        return entryProcessor(FlexibleType::getString, identity());
    }

    public static FlexibleTypeFunction<FlexibleType> identity() {
        return t -> t;
    }
    public static <T> FlexibleTypeFunction<T> getObject(Class<T> clazz) {
        return flexType -> flexType.getObject(clazz);
    }
    public static <E extends Enum<E>> FlexibleTypeFunction<E> getEnum(Class<E> clazz) {
        return flexType -> flexType.getEnum(clazz);
    }
    public static <E> FlexibleTypeFunction<List<E>> getList(FlexibleTypeFunction<E> mapper) {
        return flexType -> flexType.getList(mapper);
    }
    public static <T> T getOr(@Nullable FlexibleType type, FlexibleTypeFunction<T> mapper, T fallback) throws BadValueException {
        if(type == null) return fallback;
        return mapper.getResult(type);
    }
}
