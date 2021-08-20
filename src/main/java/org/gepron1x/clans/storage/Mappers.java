package org.gepron1x.clans.storage;

import org.gepron1x.clans.storage.mappers.column.ComponentMapper;
import org.gepron1x.clans.storage.mappers.column.ItemStackMapper;
import org.gepron1x.clans.storage.mappers.column.LocationMapper;
import org.gepron1x.clans.storage.mappers.column.UuidMapper;

public final class Mappers {
    private Mappers() { throw new UnsupportedOperationException("no instances"); }
    public static final UuidMapper UUID = new UuidMapper();
    public static final ComponentMapper COMPONENT = new ComponentMapper();
    public static final ItemStackMapper ITEM_STACK = new ItemStackMapper();
    public static final LocationMapper LOCATION = new LocationMapper();
}
