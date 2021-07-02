package com.manya.clans.storage.converters;

import com.manya.clans.statistic.StatisticType;

public record StatisticRow(String clanTag, StatisticType statType, Integer value) {
}
