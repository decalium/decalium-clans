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
package org.gepron1x.clans.api.statistic;

import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record StatisticType(@NotNull String name) {

	public static StatisticType KILLS = new StatisticType("kills");
	public static StatisticType DEATHS = new StatisticType("deaths");
	public static StatisticType CLAN_WAR_WINS = new StatisticType("clan_war_wins");
	public static StatisticType CLAN_WAR_LOSES = new StatisticType("clan_war_loses");

	public static StatisticType LEVEL = new StatisticType("level");

	private static final Index<String, StatisticType> NAMES = Index.create(StatisticType::name, KILLS, DEATHS, CLAN_WAR_WINS, CLAN_WAR_LOSES, LEVEL);

	public static Index<String, StatisticType> registry() {
		return NAMES;
	}

	public static StatisticType type(String name) {
		var value = NAMES.value(name);
		return value == null ? new StatisticType(name) : value;
	}

	public static Set<StatisticType> all() {
		return NAMES.values();
	}
}
