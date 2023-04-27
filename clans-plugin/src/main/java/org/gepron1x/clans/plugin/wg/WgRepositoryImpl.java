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
package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.WorldGuard;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.AdaptingClanRepository;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public class WgRepositoryImpl extends AdaptingClanRepository {

    private final ClansConfig clansConfig;
	private final WorldGuard worldGuard;
	private final GlobalRegions regions;
	private final FactoryOfTheFuture futuresFactory;

	public WgRepositoryImpl(ClanRepository repository, Configs configs, WorldGuard worldGuard, GlobalRegions regions, FactoryOfTheFuture futuresFactory) {
        super(repository, clan -> new WgClan(clan, configs,  worldGuard.getPlatform().getRegionContainer(), regions.clanRegions(clan), futuresFactory));
        this.clansConfig = configs.config();
		this.worldGuard = worldGuard;
		this.regions = regions;
		this.futuresFactory = futuresFactory;
	}

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
		var regionSet = regions.clanRegions(clan).regions().thenApply(set -> new WgRegionSet(worldGuard.getPlatform().getRegionContainer(), set));
		var removal = super.removeClan(clan);
		return futuresFactory.allOf(regionSet, removal).thenApply($ -> {
			regionSet.join().clear();
			return removal.join();
		});
    }
}
