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
package org.gepron1x.clans.plugin.storage.implementation.sql.common;

import org.bukkit.Location;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class SavableHomes implements Savable {

	private static final String INSERT_HOME = "INSERT INTO homes (clan_id, name, display_name, creator, icon) VALUES (?, ?, ?, ?, ?)";
	private static final String INSERT_LOCATION = "INSERT INTO `locations` (`home_id`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)";

	private final int clanId;
	private final Collection<? extends ClanHome> homes;

	public SavableHomes(int clanId, Collection<? extends ClanHome> homes) {

		this.clanId = clanId;
		this.homes = homes;
	}

	public SavableHomes(Handle handle, int clanId, ClanHome home) {
		this(clanId, Collections.singletonList(home));
	}

	@Override
	public int execute(Handle handle) {
		PreparedBatch batch = handle.prepareBatch(INSERT_HOME);
		List<ClanHome> homeList = List.copyOf(homes);
		for (ClanHome home : homeList) {
			batch.add(this.clanId, home.name(), home.displayName(), home.creator(), home.icon());
		}

		PreparedBatch locations = handle.prepareBatch(INSERT_LOCATION);
		List<Integer> ids = batch.executeAndReturnGeneratedKeys("id").mapTo(Integer.class).list();
		if (homeList.size() != ids.size()) {
			handle.rollback();
			throw new IllegalStateException("Homes with some names already exists for this clan.");
		}

		for (int i = 0; i < ids.size(); i++) {
			ClanHome home = homeList.get(i);
			int id = ids.get(i);
			Location location = home.location();
			locations.add(id, location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
		}

		return Arrays.stream(locations.execute()).sum();
	}
}
