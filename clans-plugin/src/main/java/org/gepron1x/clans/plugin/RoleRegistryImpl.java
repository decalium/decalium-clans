package org.gepron1x.clans.plugin;

import com.google.common.base.Preconditions;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.plugin.util.MapOf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class RoleRegistryImpl implements RoleRegistry {

    private final Map<String, ClanRole> roleMap;

    private final ClanRole defaultRole;
    private final ClanRole ownerRole;

    public RoleRegistryImpl(@NotNull ClanRole defaultRole, @NotNull ClanRole ownerRole, @NotNull Collection<ClanRole> roles) {
        this.defaultRole = defaultRole;
        this.ownerRole = ownerRole;
        Preconditions.checkArgument(roles.containsAll(Set.of(defaultRole, ownerRole)), "roles collection should contain default and owner role.");
        this.roleMap = new MapOf<>(ClanRole::name, roles).create();
    }
    @Override
    public @NotNull ClanRole defaultRole() {
        return defaultRole;
    }

    @Override
    public @NotNull ClanRole ownerRole() {
        return ownerRole;
    }

    @Override
    public Optional<ClanRole> value(@NotNull String name) {
        return Optional.ofNullable(roleMap.get(name));
    }

    @Override
    public @NotNull @Unmodifiable Map<String, ClanRole> asMap() {
        return this.roleMap;
    }

    @Override
    public @NotNull @Unmodifiable Collection<ClanRole> values() {
        return roleMap.values();
    }

}
