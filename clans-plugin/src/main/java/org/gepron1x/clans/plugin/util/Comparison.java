package org.gepron1x.clans.plugin.util;

public record Comparison<T extends Comparable<T>>(T left, T right) {

    public boolean lowerThan() {
        return left.compareTo(right) < 0;
    }

    public boolean greaterThan() {
        return left.compareTo(right) > 0;
    }

    public boolean greaterThanOrEqual() {
        return left.compareTo(right) <= 0;
    }

    public boolean lowerThanOrEqual() {
        return left.compareTo(right) >= 0;
    }

}

