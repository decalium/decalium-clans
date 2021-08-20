package org.gepron1x.clans.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Functions {
    private Functions() {throw new UnsupportedOperationException("ok"); }
    public static <T, V> Function<T, V> supplier(Supplier<V> supplier) {
        return t -> supplier.get();
    }
    public static <T, V> Function<T, V> consumer(Consumer<T> consumer) {
        return t -> {
            consumer.accept(t);
            return null;
        };
    }

}
