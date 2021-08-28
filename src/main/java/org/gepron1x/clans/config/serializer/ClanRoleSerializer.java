package org.gepron1x.clans.config.serializer;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.util.Configs;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.Map;

public class ClanRoleSerializer implements ValueSerialiser<ClanRole> {
    public static final String ID = "id", WEIGHT = "weight", DISPLAY_NAME = "displayName", PERMISSIONS = "permissions";

    @Override
    public Class<ClanRole> getTargetClass() {
        return ClanRole.class;
    }

    @Override
    public ClanRole deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, FlexibleType> serialized = flexibleType.getMap(Configs.section());
        return new ClanRole(serialized.get(ID).getString(),
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
