/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
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
package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.shield.CachingShields;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.api.war.Wars;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public final class DecaliumClansApiImpl implements DecaliumClansApi {

    private final CachingClanRepository clanRepository;
    private final Users users;
    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;
    private final FactoryOfTheFuture futuresFactory;
    private final Wars wars;
    private final CachingShields shields;

    public DecaliumClansApiImpl(@NotNull CachingClanRepository clanRepository,
                                @NotNull Users users,
                                @NotNull RoleRegistry roleRegistry,
                                @NotNull ClanBuilderFactory builderFactory,
                                @NotNull FactoryOfTheFuture futuresFactory,
                                @NotNull Wars wars,
                                @NotNull CachingShields shields
                                ) {

        this.clanRepository = clanRepository;
        this.users = users;
        this.roleRegistry = roleRegistry;
        this.builderFactory = builderFactory;
        this.futuresFactory = futuresFactory;
        this.wars = wars;
        this.shields = shields;
    }

    @Override
    public @NotNull Users users() {
        return users;
    }

    @Override
    public @NotNull CachingClanRepository repository() {
        return clanRepository;
    }

    @Override
    public @NotNull CachingShields shields() {
        return shields;
    }

    @Override
    public @NotNull FactoryOfTheFuture futuresFactory() {
        return futuresFactory;
    }

    @Override
    public @NotNull ClanBuilderFactory builderFactory() {
        return builderFactory;
    }


    @Override
    public @NotNull RoleRegistry roleRegistry() {
        return roleRegistry;
    }

    @Override
    public @NotNull Wars wars() {
        return wars;
    }

}
