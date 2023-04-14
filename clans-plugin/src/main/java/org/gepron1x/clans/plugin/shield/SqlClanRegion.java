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
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.plugin.storage.implementation.sql.AsyncJdbi;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.time.Duration;
import java.time.Instant;

public final class SqlClanRegion implements ClanRegion {

	private final int id;


	private final Region region;
	private transient final AsyncJdbi jdbi;

	public SqlClanRegion(int id, Region region, AsyncJdbi jdbi) {

		this.id = id;
		this.region = region;
		this.jdbi = jdbi;
	}
	@Override
	public int id() {
		return id;
	}

	@Override
	public int level() {
		return this.region.level();
	}

	@Override
	public Location location() {
		return this.region.location().clone();
	}

	@Override
	public CentralisedFuture<ClanRegion> upgrade() {
		return jdbi.useHandle(handle -> handle.execute("UPDATE `regions` SET `level`=`level`+1 WHERE `id`=?", id))
				.thenApply(ignored -> {
			return new SqlClanRegion(id, region.upgrade(), jdbi);
		});

	}

	@Override
	public Shield shield() {
		return region.shield();
	}

	@Override
	public CentralisedFuture<ClanRegion> addShield(Duration duration) {
		Instant now = Instant.now();
		Instant end = now.plus(duration);
		return jdbi.withHandle(handle -> {
			handle.createUpdate("INSERT INTO shields (`region_id`, `start`, `end`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `start`=VALUES(`start`), `end`=VALUES(`end`)")
					.bind(0, id)
					.bind(1, now)
					.bind(2, end).execute();
			return new SqlClanRegion(id, region.withShield(new ShieldImpl(now, end)), jdbi);
		});
	}
}
