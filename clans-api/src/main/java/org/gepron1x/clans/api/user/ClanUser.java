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
package org.gepron1x.clans.api.user;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.gepron1x.clans.api.shield.ClanRegions;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;

public interface ClanUser {

	default boolean isIn(Clan clan) {
		return clan().map(c -> c.id() == clan.id()).orElse(false);
	}

	default boolean hasPermission(ClanPermission permission) {
		return member().map(member -> member.hasPermission(permission)).orElse(false);
	}

    Optional<ClanRegions> regions();

    Optional<Clan> clan();

	Optional<ClanMember> member();
    
    CentralisedFuture<ClanCreationResult> create(DraftClan draft);

    CentralisedFuture<Boolean> delete();


}
