package org.gepron1x.clans.statistic;



import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public interface IntStatisticContainer {
    void setStatistic(StatisticType type, int value);
    boolean hasStatistic(StatisticType type);
    OptionalInt getStatistic(StatisticType type);
    void removeStatistic(StatisticType type);
}
