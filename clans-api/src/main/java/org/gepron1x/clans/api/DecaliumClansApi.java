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
package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.economy.LevelsMeta;
import org.gepron1x.clans.api.economy.Prices;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.api.war.Wars;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public interface DecaliumClansApi extends ClanBuilderFactory {

	@NotNull Users users();

	@NotNull CachingClanRepository repository();

	@NotNull GlobalRegions regions();

	@NotNull FactoryOfTheFuture futuresFactory();


	@NotNull ClanBuilderFactory builderFactory();

	@NotNull RoleRegistry roleRegistry();

	@NotNull Wars wars();

	@NotNull Prices prices();

	@NotNull LevelsMeta levels();


	// im lazy
	@Override
	@NotNull
	default DraftClan.Builder draftClanBuilder() {
		return builderFactory().draftClanBuilder();
	}

	@Override
	@NotNull
	default ClanMember.Builder memberBuilder() {
		return builderFactory().memberBuilder();
	}

	@Override
	@NotNull
	default ClanHome.Builder homeBuilder() {
		return builderFactory().homeBuilder();
	}

	@Override
	@NotNull
	default ClanRole.Builder roleBuilder() {
		return builderFactory().roleBuilder();
	}
}
