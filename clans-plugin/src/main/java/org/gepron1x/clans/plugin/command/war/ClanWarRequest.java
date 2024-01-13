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
package org.gepron1x.clans.plugin.command.war;

import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.util.player.PlayerReference;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.api.war.Wars;

public record ClanWarRequest(Wars wars, PlayerReference actor, ClanReference actorClan, ClanReference victimClan) {


	public void accept() {
		Team first = wars.createTeam(actorClan);
		Team second = wars.createTeam(victimClan);
		wars.start(wars.create(first, second));

	}


}
