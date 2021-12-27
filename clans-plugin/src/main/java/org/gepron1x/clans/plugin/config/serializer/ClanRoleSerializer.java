package org.gepron1x.clans.plugin.config.serializer;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ClanRoleSerializer implements ValueSerialiser<ClanRole> {

    private static final String NAME = "name", DISPLAY_NAME = "display_name", WEIGHT = "weight", PERMISSIONS = "permissions";
    private final ClanBuilderFactory builderFactory;


    public ClanRoleSerializer(@NotNull ClanBuilderFactory builderFactory) {

        this.builderFactory = builderFactory;
    }
    @Override
    public Class<ClanRole> getTargetClass() {
        return ClanRole.class;
    }

    @Override
    public ClanRole deserialise(FlexibleType flexibleType) throws BadValueException {
        ClanRole.Builder builder = builderFactory.roleBuilder();
        Map<String, FlexibleType> map = flexibleType.getMap((key, value) -> Map.entry(key.getString(), value));
        return builder.name(map.get(NAME).getString())
                .displayName(map.get(DISPLAY_NAME).getObject(Component.class))
                .weight(map.get(WEIGHT).getInteger())
                .permissions(map.get(PERMISSIONS).getList(flexType -> flexType.getObject(ClanPermission.class)))
                .build();
    }

    @Override
    public Map<String, Object> serialise(ClanRole value, Decomposer decomposer) {
        Map<String, Object> map = new LinkedHashMap<>(4);

        map.put(NAME, value.getName());
        map.put(DISPLAY_NAME, decomposer.decompose(Component.class, value.getDisplayName()));
        map.put(WEIGHT, value.getWeight());
        map.put(PERMISSIONS, decomposer.decomposeCollection(ClanPermission.class, value.getPermissions()));

        return map;
    }
}
