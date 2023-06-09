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
package org.gepron1x.clans.plugin.cache;

import org.bukkit.Server;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.ClanRepository;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.UUID;

public final class UserCaching {

    private final ClanRepository repository;
    private final ClanCache cache;
    private final Server server;
	private final FactoryOfTheFuture futures;

	public UserCaching(ClanRepository repository, ClanCache cache, Server server, FactoryOfTheFuture futures) {
        this.repository = repository;
        this.cache = cache;
        this.server = server;
		this.futures = futures;
	}

    public void cacheUser(UUID uniqueId) {
        if(cache.getUserClan(uniqueId) != null) return;
        this.repository.requestUserClan(uniqueId).join().ifPresent(clan -> cache.cacheClan(new CachingClan(clan, cache, futures)));
    }

    public void remove(UUID uniqueId) {
        Clan clan = cache.getUserClan(uniqueId);
        if(clan == null) return;
        if(areMembersOnline(clan)) return;
        cache.removeClan(clan.tag());
    }

    private boolean areMembersOnline(Clan clan) {
        for(UUID uuid : clan.memberMap().keySet()) {
            if(server.getPlayer(uuid) != null) return true;
        }
        return false;
    }
}
