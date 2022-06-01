package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public interface RoleRegistry {
    @NotNull ClanRole defaultRole();
    @NotNull ClanRole ownerRole();

    @Nullable ClanRole role(@NotNull String name);

    @NotNull @Unmodifiable Collection<ClanRole> roles();

}
