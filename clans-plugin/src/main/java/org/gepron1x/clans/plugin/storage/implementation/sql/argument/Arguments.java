package org.gepron1x.clans.plugin.storage.implementation.sql.argument;


public final class Arguments {
    private Arguments() {}
    public static final ComponentArgumentFactory COMPONENT = new ComponentArgumentFactory();
    public static final ItemStackArgumentFactory ITEM_STACK = new ItemStackArgumentFactory();
    public static final UuidArgumentFactory UUID = new UuidArgumentFactory();
}
