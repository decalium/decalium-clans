/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
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

    @NotNull String name();
    @NotNull Component displayName();
    int weight();
    @NotNull @Unmodifiable Set<? extends ClanPermission> permissions();

    @Override
    @NotNull
    default Component asComponent() {
        return displayName();
    }

    @Override
    default int compareTo(@NotNull ClanRole clanRole) {
        return weight() - clanRole.weight();
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
