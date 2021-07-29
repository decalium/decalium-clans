package org.gepron1x.clans.storage.converters;

import org.gepron1x.clans.storage.converters.component.ComponentMapper;
import org.gepron1x.clans.storage.converters.uuid.UuidMapper;

public final class Mappers {
    private Mappers() {throw new UnsupportedOperationException("no isntances"); }
    public static final UuidMapper UUID = new UuidMapper();
    public static final ComponentMapper COMPONENT = new ComponentMapper();
}
