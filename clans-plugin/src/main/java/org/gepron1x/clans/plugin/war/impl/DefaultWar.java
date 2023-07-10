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
package org.gepron1x.clans.plugin.war.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.api.war.War;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public final class DefaultWar implements War {

	private final Collection<Team> teams;

	public DefaultWar(Collection<Team> teams) {
		this.teams = teams;
	}

	@Override
	public Team enemy(Team team) {
		Preconditions.checkArgument(this.teams.contains(team), "Team is not in the war");
		Iterator<Team> iterator = Iterators.cycle(this.teams);
		Team next = iterator.next();
		while (next.equals(team)) {
			next = iterator.next();
		}
		return next;
	}

	@Override
	public Collection<Team> teams() {
		return Collections.unmodifiableCollection(teams);
	}

	@Override
	public boolean onPlayerDeath(Player player) {
		boolean ok = false;
		for (Team team : teams) {
			ok |= team.onDeath(player);
		}
		return ok;
	}

	@Override
	public boolean teamWon() {
		for (Team team : teams) {
			if (!team.isAlive()) return true;
		}
		return false;
	}

	@Override
	public void finish() {
		for (Team team : teams) {
			final StatisticType type = team.isAlive() ? StatisticType.CLAN_WAR_WINS : StatisticType.CLAN_WAR_LOSES;
			team.clan().edit(edition -> edition.incrementStatistic(type));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultWar that = (DefaultWar) o;
		return teams.equals(that.teams);
	}

	@Override
	public int hashCode() {
		return Objects.hash(teams);
	}
}
