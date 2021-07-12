package org.gepron1x.clans.statistic;

import net.kyori.adventure.text.Component;

public class StatisticType {
    public static final StatisticType
            KILLS = new StatisticType("kills", Component.text("Убийства")),
            DEATHS = new StatisticType("deaths", Component.text("Смерти")),
            CLAN_WAR_WINS = new StatisticType("clan_war_wins", Component.text("Победы в кв")),
            CLAN_WAR_LOSES = new StatisticType("clan_war_loses", Component.text("Поражения в кв"));

    private final String name;
    private Component displayName;


    public StatisticType(String name, Component displayName) {
        this.name = name;
        this.displayName = displayName;
    }


    public String getName() {
        return name;
    }



    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

}
