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

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AdaptingClanRepository implements ClanRepository {

    private final ClanRepository repository;
    private final Function<Clan, ? extends Clan> mappingFunction;

    public AdaptingClanRepository(ClanRepository repository, Function<Clan, ? extends Clan> mappingFunction) {
        this.repository = repository;
        this.mappingFunction = mappingFunction;
    }


    @Override
    public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
        return this.repository.createClan(draftClan)
                .thenApply(result ->
                        new ClanCreationResult(result.isSuccess() ? mappingFunction.apply(result.orElseThrow()) : null, result.status())
                );
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return this.repository.removeClan(clan);
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> requestClan(@NotNull String tag) {
        return this.repository.requestClan(tag).thenApply(optional -> optional.map(mappingFunction));
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull UUID uuid) {
        return this.repository.requestUserClan(uuid).thenApply(optional -> optional.map(mappingFunction));
    }

    @Override
    public @NotNull CentralisedFuture<Set<? extends Clan>> clans() {
        return this.repository.clans().thenApply(clans -> clans.stream().map(mappingFunction).collect(Collectors.toUnmodifiableSet()));
    }

}
