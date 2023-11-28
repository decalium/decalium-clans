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
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.Shield;
import org.gepron1x.clans.api.region.effect.ActiveEffect;
import org.gepron1x.clans.api.region.effect.RegionEffect;
import org.gepron1x.clans.plugin.storage.implementation.sql.SqlQueue;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public final class SqlClanRegion implements ClanRegion {


	private final ClanRegion region;
	private final SqlQueue queue;

	public SqlClanRegion(ClanRegion region, SqlQueue queue) {
		this.region = region;
		this.queue = queue;

	}

	@Override
	public int id() {
		return region.id();
	}

	@Override
	public ClanReference clan() {
		return region.clan();
	}


	@Override
	public Location location() {
		return this.region.location().clone();
	}


	@Override
	public Shield shield() {
		return region.shield();
	}

	@Override
	public Shield addShield(Duration duration) {
		var shield = region.addShield(duration);

		queue.add(handle -> {
			handle.createUpdate("INSERT INTO region_shields (`region_id`, `start`, `end`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `start`=VALUES(`start`), `end`=VALUES(`end`)")
					.bind(0, region.id())
					.bind(1, shield.started())
					.bind(2, shield.end()).execute();
		});
		return shield;
	}

	@Override
	public ActiveEffect applyEffect(RegionEffect effect, Duration duration) {
		var activeEffect = region.applyEffect(effect, duration);
		queue.add(handle -> {
			handle.createUpdate("INSERT INTO region_effects (`region_id`, `end`, `type`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `end`=VALUES(`end`), `type`=VALUES(`type`)")
					.bind(0, region.id())
					.bind(1, activeEffect.end())
					.bind(2, effect.name()).execute();
		});
		return activeEffect;
	}

	@Override
	public Optional<ActiveEffect> activeEffect() {
		return region.activeEffect();
	}

	@Override
	public void removeShield() {
		region.removeShield();
		queue.add(handle -> {
			handle.execute("DELETE FROM region_shields WHERE `region_id`=?", region.id());
		});
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SqlClanRegion that = (SqlClanRegion) o;
		return Objects.equals(region, that.region) && Objects.equals(queue, that.queue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(region, queue);
	}
}
