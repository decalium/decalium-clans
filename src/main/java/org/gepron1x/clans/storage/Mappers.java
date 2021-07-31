package org.gepron1x.clans.storage;

import org.gepron1x.clans.storage.mappers.column.ComponentMapper;
import org.gepron1x.clans.storage.mappers.column.UuidMapper;

public final class Mappers {
    private Mappers() {throw new UnsupportedOperationException("no isntances"); }
    public static final UuidMapper UUID = new UuidMapper();
    public static final ComponentMapper COMPONENT = new ComponentMapper();
}
