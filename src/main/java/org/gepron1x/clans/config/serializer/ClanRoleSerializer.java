package org.gepron1x.clans.config.serializer;

import org.gepron1x.clans.clan.role.ClanPermission;
import org.gepron1x.clans.clan.role.ClanRole;
import net.kyori.adventure.text.Component;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.Map;

public class ClanRoleSerializer implements ValueSerialiser<ClanRole> {
    public static final ClanRoleSerializer INSTANCE = new ClanRoleSerializer();
    public static final String ID = "id", WEIGHT = "weight", DISPLAY_NAME = "displayName", PERMISSIONS = "permissions";

    @Override
    public Class<ClanRole> getTargetClass() {
        return ClanRole.class;
    }

    @Override
    public ClanRole deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, FlexibleType> serialized = flexibleType.getMap((key, value) -> Map.entry(key.getString(), value));

        return new ClanRole(serialized.get(ID).getString(), // role identifier (user, owner)
                serialized.get(DISPLAY_NAME).getObject(Component.class),
                serialized.get(WEIGHT).getInteger(),
                serialized.get(PERMISSIONS).getCollection(element -> element.getObject(ClanPermission.class)));
    }

    @Override
    public Map<String, Object> serialise(ClanRole value, Decomposer decomposer) {
        return Map.of(
                ID, value.getName(),
                WEIGHT, value.getWeight(),
                DISPLAY_NAME, decomposer.decompose(Component.class, value.getDisplayName()),
                PERMISSIONS, decomposer.decomposeCollection(ClanPermission.class, value.getPermissions())
        );
    }

}
