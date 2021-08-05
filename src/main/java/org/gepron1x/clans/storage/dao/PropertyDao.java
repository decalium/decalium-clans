package org.gepron1x.clans.storage.dao;

import org.gepron1x.clans.events.Property;
import org.jdbi.v3.sqlobject.SqlObject;

public interface PropertyDao extends SqlObject {
    void updateProperty(Property<?, ?> property, Object target, Object value);

}
