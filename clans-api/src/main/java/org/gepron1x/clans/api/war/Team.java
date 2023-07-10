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
package org.gepron1x.clans.api.war;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.chat.GroupAudience;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.util.player.PlayerReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Team extends GroupAudience {

	ClanReference clan();

	Collection<PlayerReference> members();

	default boolean isMember(Player player) {
		for (PlayerReference ref : members()) {
			if (ref.player().map(p -> p.equals(player)).orElse(false)) return true;
		}
		return false;
	}

	Collection<PlayerReference> alive();

	boolean onDeath(Player player);

	boolean isAlive();

	@Override
	@NotNull
	default Iterable<? extends Audience> audiences() {
		return this.members();
	}
}
