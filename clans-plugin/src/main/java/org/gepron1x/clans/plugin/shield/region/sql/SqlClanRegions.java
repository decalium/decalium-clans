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
package org.gepron1x.clans.plugin.shield.region.sql;

import org.bukkit.Location;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.plugin.storage.implementation.sql.SqlQueue;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class SqlClanRegions implements ClanRegions {


	private final ClanRegions regions;
	private final int clanId;

	private final SqlQueue queue;

	public SqlClanRegions(ClanRegions regions, int clanId, SqlQueue queue) {
		this.regions = regions;
		this.clanId = clanId;
		this.queue = queue;
	}

	@Override
	public Set<ClanRegion> regions() {
		return regions.regions().stream().map(r -> new SqlClanRegion(r, queue)).collect(Collectors.toSet());
	}

	@Override
	public Optional<ClanRegion> region(int id) {
		return regions.region(id).map(region -> new SqlClanRegion(region, queue));
	}

	@Override
	public Optional<ClanRegion> region(Location location) {
		return regions.region(location).map(region -> new SqlClanRegion(region, queue));
	}

	@Override
	public void remove(ClanRegion region) {
		regions.remove(region);
		queue.add(handle -> handle.execute("DELETE FROM `regions` WHERE `id`=?", region.id()));
	}


	@Override
	public ClanRegion create(Location location) {
		ClanRegion region = regions.create(location);
		queue.add(handle -> {
			handle.createUpdate("INSERT INTO `regions` (id, clan_id, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?)")
					.bind(0, region.id())
					.bind(1, clanId)
					.bind(2, location.getBlockX())
					.bind(3, location.getBlockY())
					.bind(4, location.getBlockZ())
					.bind(5, location.getWorld().getName())
					.execute();
		});
		return new SqlClanRegion(region, queue);
	}

	@Override
	public void clear() {
		regions.clear();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SqlClanRegions that = (SqlClanRegions) o;
		return clanId == that.clanId && Objects.equals(regions, that.regions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(regions, clanId);
	}
}
