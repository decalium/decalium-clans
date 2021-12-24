package org.gepron1x.clans.plugin.util;

import org.jetbrains.annotations.NotNull;

public final class Compare {
    private Compare() { throw new UnsupportedOperationException(); }

    public static <T extends Comparable<T>> boolean lt(@NotNull T left, @NotNull T right) {
        return left.compareTo(right) < 0;
    }

    public static <T extends Comparable<T>> boolean gt(@NotNull T left, @NotNull T right) {
        return left.compareTo(right) > 0;
    }

    public static <T extends Comparable<T>> boolean lte(@NotNull T left, @NotNull T right) {
        return left.compareTo(right) <= 0;
    }

    public static <T extends Comparable<T>> boolean gte(@NotNull T left, @NotNull T right) {
        return left.compareTo(right) >= 0;
    }

}
