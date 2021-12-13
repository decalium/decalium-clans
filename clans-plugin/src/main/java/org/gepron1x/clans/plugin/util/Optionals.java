package org.gepron1x.clans.plugin.util;

import org.jetbrains.annotations.Nullable;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public final class Optionals {
    private Optionals() {
        throw new UnsupportedOperationException();
    }

    public static OptionalInt ofNullable(@Nullable Integer value) {
        return value == null ? OptionalInt.empty() : OptionalInt.of(value);

    }

    public static OptionalLong ofNullable(@Nullable Long value) {
        return value == null ? OptionalLong.empty() : OptionalLong.of(value);
    }

    public static OptionalDouble ofNullable(@Nullable Double value) {
        return value == null ? OptionalDouble.empty() : OptionalDouble.of(value);
    }
}
