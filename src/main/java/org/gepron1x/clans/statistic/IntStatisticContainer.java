package org.gepron1x.clans.statistic;



import java.util.OptionalInt;

public interface IntStatisticContainer {
    void setStatistic(StatisticType type, int value);
    default void incrementStatistic(StatisticType type) {
        setStatistic(type, getStatistic(type).orElse(0) + 1);
    }
    default void decrementStatistic(StatisticType type) {
        setStatistic(type, Math.max(0, getStatistic(type).orElse(0) - 1));
    }
    default int getStatisticOr(StatisticType type, int fallback) {
        return getStatistic(type).orElse(fallback);
    }

    boolean hasStatistic(StatisticType type);
    OptionalInt getStatistic(StatisticType type);
    void removeStatistic(StatisticType type);
}
