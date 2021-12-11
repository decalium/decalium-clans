package org.gepron1x.clans.api.statistic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.OptionalInt;

public interface StatisticHolder {
    OptionalInt getStatistic(@NotNull StatisticType type);

    default int getStatisticOr(@NotNull StatisticType type, int fallback) {
        return getStatistic(type).orElse(fallback);
    }


    @NotNull
    @Unmodifiable
    Map<StatisticType, Integer> getStatistics();
}
