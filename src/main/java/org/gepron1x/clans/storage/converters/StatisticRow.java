package org.gepron1x.clans.storage.converters;

import org.gepron1x.clans.statistic.StatisticType;

public record StatisticRow(String clanTag, StatisticType statType, Integer value) {
}
