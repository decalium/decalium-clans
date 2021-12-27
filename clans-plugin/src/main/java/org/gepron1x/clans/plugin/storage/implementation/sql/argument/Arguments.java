package org.gepron1x.clans.plugin.storage.implementation.sql.argument;


public final class Arguments {
    private Arguments() {}
    public static final ComponentArgumentFactory COMPONENT = new ComponentArgumentFactory();
    public static final ItemStackArgumentFactory ITEM_STACK = new ItemStackArgumentFactory();
    public static final UuidArgumentFactory UUID = new UuidArgumentFactory();
    public static final ClanRoleArgumentFactory CLAN_ROLE = new ClanRoleArgumentFactory();
    public static final StatisticTypeArgumentFactory STATISTIC_TYPE = new StatisticTypeArgumentFactory();
}
