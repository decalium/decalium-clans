package org.gepron1x.clans.util;

import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.FlexibleTypeFunction;
import space.arim.dazzleconf.serialiser.FlexibleTypeMapEntryFunction;

import java.util.Map;

public class Configs {

    public static <K, V> FlexibleTypeMapEntryFunction<? extends K, ? extends V> entryProcessor(FlexibleTypeFunction<? extends K> keyProcessor,
                                                                                               FlexibleTypeFunction<? extends V> valueProcessor) {
        return (key, value) -> Map.entry(keyProcessor.getResult(key), valueProcessor.getResult(value));
    }

    public static FlexibleTypeMapEntryFunction<? extends String, ? extends FlexibleType> section() {
        return entryProcessor(FlexibleType::getString, identity());
    }

    public static FlexibleTypeFunction<FlexibleType> identity() {
        return t -> t;
    }
}
