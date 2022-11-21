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
package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;

import java.util.List;

public final class WgExtension {

    public static final StringFlag CLAN = new StringFlag("clan");
    public static final StringFlag HOME_NAME = new StringFlag("clan-home-name");

    public static final BooleanFlag SHIELD_ACTIVE = new BooleanFlag("clan-shield-active");
    private final ClansConfig clansConfig;
    private final ClanRepository repository;
    private final RegionFactory regionFactory;

    public WgExtension(ClansConfig clansConfig, ClanRepository repository, RegionFactory regionFactory) {
        this.clansConfig = clansConfig;
        this.repository = repository;
        this.regionFactory = regionFactory;
    }


    public ClanRepository make() {
        return new WgRepositoryImpl(this.repository, this.clansConfig,regionFactory);
    }

    public static void registerFlags() {
        WorldGuard.getInstance().getFlagRegistry().registerAll(List.of(CLAN, HOME_NAME, SHIELD_ACTIVE));
    }

}
