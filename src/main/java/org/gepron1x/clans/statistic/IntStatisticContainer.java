package org.gepron1x.clans.statistic;



import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.OptionalInt;

public interface IntStatisticContainer {
    void setStatistic(@NotNull StatisticType type, int value);
    default void incrementStatistic(@NotNull StatisticType type) {
        setStatistic(type, getStatistic(type).orElse(0) + 1);
    }
    default void decrementStatistic(@NotNull StatisticType type) {
        setStatistic(type, Math.max(0, getStatistic(type).orElse(0) - 1));
    }
    default int getStatisticOr(@NotNull StatisticType type, int fallback) {
        return getStatistic(type).orElse(fallback);
    }

    default boolean hasStatistic(@NotNull StatisticType type) {
        return getStatistic(type).isPresent();
    }
    OptionalInt getStatistic(@NotNull StatisticType type);
    @NotNull
    @UnmodifiableView
    Map<StatisticType, Integer> getStatistics();

    void removeStatistic(@NotNull StatisticType type);
}
