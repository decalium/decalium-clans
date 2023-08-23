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
package org.gepron1x.clans.api.region;

import org.bukkit.Location;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.region.effect.ActiveEffect;
import org.gepron1x.clans.api.region.effect.RegionEffect;

import java.time.Duration;
import java.util.Optional;

public interface ClanRegion {

	int id();

	ClanReference clan();

	Location location();

	Shield shield();


	Shield addShield(Duration duration);

	ActiveEffect applyEffect(RegionEffect effect, Duration duration);

	Optional<ActiveEffect> activeEffect();

	void removeShield();
}
