package org.gepron1x.clans.api.statistic;

import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record StatisticType(@NotNull String name) {

    public static StatisticType KILLS = new StatisticType("kills");
    public static StatisticType DEATHS = new StatisticType("deaths");
    public static StatisticType CLAN_WAR_WINS = new StatisticType("clan_war_wins");
    public static StatisticType CLAN_WAR_LOSES = new StatisticType("clan_war_loses");

    private static final Index<String, StatisticType> NAMES = Index.create(StatisticType::name, KILLS, DEATHS, CLAN_WAR_WINS, CLAN_WAR_LOSES);

    public static Index<String, StatisticType> registry() {
        return NAMES;
    }

    public static Set<StatisticType> all() {
        return NAMES.values();
    }
}
