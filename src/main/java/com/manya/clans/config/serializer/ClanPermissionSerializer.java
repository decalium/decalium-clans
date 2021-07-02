package com.manya.clans.config.serializer;

import com.manya.clans.clan.role.ClanPermission;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public class ClanPermissionSerializer implements ValueSerialiser<ClanPermission> {
    public static final ClanPermissionSerializer INSTANCE = new ClanPermissionSerializer();

    @Override
    public Class<ClanPermission> getTargetClass() {
        return ClanPermission.class;
    }

    @Override
    public ClanPermission deserialise(FlexibleType flexibleType) throws BadValueException {
        return new ClanPermission(flexibleType.getString());

    }

    @Override
    public String serialise(ClanPermission value, Decomposer decomposer) {
        return value.value();
    }
    public static ClanPermissionSerializer getInstance() {
        return INSTANCE;
    }
}
