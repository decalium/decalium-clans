package org.gepron1x.clans.api.clan.member;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.util.Buildable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public interface ClanRole extends Comparable<ClanRole>, Buildable<ClanRole, ClanRole.Builder>, ComponentLike {

    @NotNull String getName();
    @NotNull Component getDisplayName();
    int getWeight();
    @NotNull @Unmodifiable Set<? extends ClanPermission> getPermissions();

    @Override
    @NotNull
    default Component asComponent() {
        return getDisplayName();
    }

    @Override
    default int compareTo(@NotNull ClanRole clanRole) {
        return getWeight() - clanRole.getWeight();
    }

    interface Builder extends Buildable.Builder<ClanRole> {
        @Contract("_ -> this")
        @NotNull Builder name(@NotNull String name);

        @Contract("_ -> this")
        @NotNull Builder displayName(@NotNull Component displayName);

        @Contract("_ -> this")
        @NotNull Builder weight(int weight);

        @Contract("_ -> this")
        @NotNull Builder permissions(@NotNull Collection<? extends ClanPermission> permissions);

        @Contract("_ -> this")
        default @NotNull Builder permissions(@NotNull ClanPermission @NotNull... permissions) {
            return permissions(Arrays.asList(permissions));
        }

        @Contract("_ -> this")
        @NotNull Builder addPermission(@NotNull ClanPermission permission);

        @NotNull Builder emptyPermissions();


    }




}
