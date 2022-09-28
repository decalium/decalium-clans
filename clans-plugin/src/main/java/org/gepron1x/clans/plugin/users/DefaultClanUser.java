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
package org.gepron1x.clans.plugin.users;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.gepron1x.clans.api.user.ClanUser;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.UUID;

public final class DefaultClanUser implements ClanUser {

    private final CachingClanRepository repository;
    private final UUID uuid;

    public DefaultClanUser(CachingClanRepository repository, UUID uuid) {
        this.repository = repository;
        this.uuid = uuid;
    }
    @Override
    public Optional<Clan> clan() {
        return repository.userClanIfCached(uuid);
    }

    @Override
    public CentralisedFuture<ClanCreationResult> create(DraftClan draft) {
        return this.repository.createClan(draft);
    }

    @Override
    public CentralisedFuture<Boolean> delete() {
        return repository.removeClan(clan().orElseThrow());
    }
}
