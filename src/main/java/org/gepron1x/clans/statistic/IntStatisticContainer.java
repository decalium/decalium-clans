package org.gepron1x.clans.statistic;



import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class IntStatisticContainer {
    private final Map<StatisticType, Integer> values = new HashMap<>();

    public void setValue(StatisticType type, int value) {
        values.put(type, value);
    }
    public Integer getValue(@NotNull StatisticType type) {
        return values.get(type);
    }
    public void remove(@NotNull StatisticType type) {
        values.remove(type);
    }



}
