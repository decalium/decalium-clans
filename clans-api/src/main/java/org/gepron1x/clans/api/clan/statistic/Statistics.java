package org.gepron1x.clans.api.clan.statistic;

import org.gepron1x.clans.api.statistic.StatisticType;

import java.util.Map;
import java.util.OptionalInt;

public interface Statistics {

    Map<StatisticType, Integer> asMap();

    OptionalInt statistic(StatisticType type);

    default int statisticOrZero(StatisticType type) {
        return statistic(type).orElse(0);
    }

}
