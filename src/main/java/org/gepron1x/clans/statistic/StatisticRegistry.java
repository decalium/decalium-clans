package org.gepron1x.clans.statistic;

import org.gepron1x.clans.util.registry.HashRegistry;

import java.util.Arrays;
import java.util.Collection;


public class StatisticRegistry extends HashRegistry<String, StatisticType> {
    public StatisticRegistry(int initialCapacity) {
        super(StatisticType::getName, initialCapacity);
    }
    public static StatisticRegistry create(StatisticType... types) {
        return create(Arrays.asList(types));
    }
    public static StatisticRegistry create(Collection<StatisticType> types) {
        StatisticRegistry registry = new StatisticRegistry(types.size());
        registry.addAll(types);
        return registry;
    }
}
