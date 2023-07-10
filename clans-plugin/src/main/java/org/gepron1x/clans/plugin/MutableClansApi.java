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
package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.economy.LevelsMeta;
import org.gepron1x.clans.api.economy.Prices;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.api.war.Wars;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public final class MutableClansApi implements DecaliumClansApi {

	private DecaliumClansApi api;

	public MutableClansApi(DecaliumClansApi api) {
		this.api = api;
	}

	@Override
	public @NotNull Users users() {
		return this.api.users();
	}

	@Override
	public @NotNull CachingClanRepository repository() {
		return this.api.repository();
	}

	@Override
	public @NotNull GlobalRegions regions() {
		return this.api.regions();
	}

	@Override
	public @NotNull FactoryOfTheFuture futuresFactory() {
		return this.api.futuresFactory();
	}

	@Override
	public @NotNull ClanBuilderFactory builderFactory() {
		return this.api.builderFactory();
	}

	@Override
	public @NotNull RoleRegistry roleRegistry() {
		return this.api.roleRegistry();
	}

	@Override
	public @NotNull Wars wars() {
		return this.api.wars();
	}

	@Override
	public @NotNull Prices prices() {
		return this.api.prices();
	}

	@Override
	public @NotNull LevelsMeta levels() {
		return this.api.levels();
	}

	public void setApi(DecaliumClansApi api) {
		this.api = api;
	}
}
