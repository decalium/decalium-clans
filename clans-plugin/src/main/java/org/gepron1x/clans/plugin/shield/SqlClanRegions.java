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
package org.gepron1x.clans.plugin.shield;

import org.bukkit.Location;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.plugin.storage.implementation.sql.AsyncJdbi;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Set;

public final class SqlClanRegions implements ClanRegions {

	private final Clan clan;
	private final AsyncJdbi jdbi;

	public SqlClanRegions(Clan clan, AsyncJdbi jdbi) {
		this.clan = clan;
		this.jdbi = jdbi;
	}

	@Override
	public CentralisedFuture<Set<ClanRegion>> regions() {
		return null;
	}

	@Override
	public CentralisedFuture<ClanRegion> create(Location location) {
		return jdbi.withHandle(handle -> {
			int id = handle.createUpdate("INSERT INTO `regions` (clan_id, x, y, z, world) VALUES (?, ?, ?, ?)")
					.bind(0, clan.id())
					.bind(1, location.getBlockX())
					.bind(2, location.getBlockY())
					.bind(3, location.getBlockZ())
					.bind(4, location.getWorld().getName())
					.executeAndReturnGeneratedKeys("id")
					.mapTo(Integer.class).first();
			return new SqlClanRegion(id, new Region(0, location, Shield.NONE), jdbi);
		});
	}
}
