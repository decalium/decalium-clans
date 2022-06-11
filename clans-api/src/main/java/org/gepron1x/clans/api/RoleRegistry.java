package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;

public interface RoleRegistry extends Registry<String, ClanRole> {
    @NotNull ClanRole defaultRole();
    @NotNull ClanRole ownerRole();

    Optional<ClanRole> value(@NotNull String name);

    @NotNull @Unmodifiable Collection<ClanRole> values();

}
