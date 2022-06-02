package org.gepron1x.clans.api.statistic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.OptionalInt;

public interface StatisticHolder {
    default OptionalInt statistic(@NotNull StatisticType type) {
        Integer integer = statistics().get(type);
        return integer == null ? OptionalInt.empty() : OptionalInt.of(integer);
    }

    default int statisticOr(@NotNull StatisticType type, int fallback) {
        return statistic(type).orElse(fallback);
    }


    @NotNull
    @Unmodifiable
    Map<StatisticType, Integer> statistics();
}

