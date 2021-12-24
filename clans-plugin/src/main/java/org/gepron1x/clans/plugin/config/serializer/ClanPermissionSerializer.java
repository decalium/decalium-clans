package org.gepron1x.clans.plugin.config.serializer;

import org.gepron1x.clans.api.clan.member.ClanPermission;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public final class ClanPermissionSerializer implements ValueSerialiser<ClanPermission> {
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
}
