package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface RoleRegistry {
    @NotNull ClanRole getDefaultRole();
    @NotNull ClanRole getOwnerRole();

    @Nullable ClanRole getRole(@NotNull String name);

    @NotNull @UnmodifiableView Collection<ClanRole> getRoles();

    void addRole(@NotNull ClanRole role);
    void removeRole(@NotNull ClanRole role);






}
