package org.gepron1x.clans.storage.mappers.row;

import org.gepron1x.clans.statistic.StatisticType;

public record StatisticRow(String clanTag, StatisticType statType, Integer value) {
}
