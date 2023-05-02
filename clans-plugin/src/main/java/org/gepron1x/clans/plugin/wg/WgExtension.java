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
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.config.Configs;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.List;

public final class WgExtension {

    public static final StringFlag CLAN = new StringFlag("clan");
    public static final StringFlag HOME_NAME = new StringFlag("clan-home-name");

	public static final IntegerFlag REGION_ID = new IntegerFlag("clan-region-id");

    public static final BooleanFlag SHIELD_ACTIVE = new BooleanFlag("clan-shield-active");
    private final Configs configs;
    private final ClanRepository repository;
	private final GlobalRegions regions;
	private final FactoryOfTheFuture futuresFactory;

	public WgExtension(ClanRepository repository, Configs configs, GlobalRegions regions, FactoryOfTheFuture futuresFactory) {
        this.configs = configs;
        this.repository = repository;
		this.regions = regions;
		this.futuresFactory = futuresFactory;
	}


    public ClanRepository make() {
        return new WgRepositoryImpl(this.repository, configs, WorldGuard.getInstance(), regions, futuresFactory);
    }

    public static void registerFlags() {
        List.of(CLAN, HOME_NAME, SHIELD_ACTIVE, REGION_ID).forEach(WorldGuard.getInstance().getFlagRegistry()::register);
    }

	public static String regionName(ClanRegion region) {
		return "clans_region_"+region.id();
	}

}
