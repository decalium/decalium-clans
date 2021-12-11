package org.gepron1x.clans.api.clan.member;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface ClanRole extends Comparable<ClanRole> {

    @NotNull String getIdentifier();
    @NotNull Component getDisplayName();
    int getWeight();
    @NotNull @Unmodifiable Set<ClanPermission> getPermissions();

    @Override
    default int compareTo(@NotNull ClanRole clanRole) {
        return getWeight() - clanRole.getWeight();
    }
}
