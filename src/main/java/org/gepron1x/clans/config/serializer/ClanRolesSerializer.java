package org.gepron1x.clans.config.serializer;

import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.util.CollectionUtils;
import org.gepron1x.clans.util.Configs;
import org.gepron1x.clans.util.registry.ClanRoleRegistry;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClanRolesSerializer implements ValueSerialiser<ClanRoleRegistry> {
    private final String OWNER = "owner", DEFAULT = "default", ROLES = "roles";
    @Override
    public Class<ClanRoleRegistry> getTargetClass() {
        return ClanRoleRegistry.class;
    }

    @Override
    public ClanRoleRegistry deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, FlexibleType> map = flexibleType.getMap(Configs.section());
        String ownerKey = map.get(OWNER).getString();
        String defaultKey = map.get(DEFAULT).getString();

        Map<String, ClanRole> roles = CollectionUtils.toMap(
                ClanRole::getName,
                map.get(ROLES).getCollection(Configs.getObject(ClanRole.class))
        );

        return ClanRoleRegistry.create(roles.get(defaultKey), roles.get(ownerKey), roles.values());
    }

    @Override
    public Map<String, Object> serialise(ClanRoleRegistry value, Decomposer decomposer) {
        Map<String, Object> map = new LinkedHashMap<>(3);
        map.put(ROLES, decomposer.decomposeCollection(ClanRole.class, value.values()));
        map.put(OWNER, value.getOwnerRole().getName());
        map.put(DEFAULT, value.getDefaultRole().getName());
        return map;
    }
}
