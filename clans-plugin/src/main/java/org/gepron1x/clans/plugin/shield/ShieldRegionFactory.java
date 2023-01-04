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
package org.gepron1x.clans.plugin.shield;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.shield.CachingShields;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.gepron1x.clans.plugin.wg.RegionFactory;
import org.gepron1x.clans.plugin.wg.WgExtension;
import org.gepron1x.clans.plugin.wg.WgHome;

public final class ShieldRegionFactory implements RegionFactory {

    private final RegionFactory regionFactory;
    private final CachingShields shields;
    private final ClansConfig config;

    public ShieldRegionFactory(RegionFactory regionFactory, CachingShields shields, ClansConfig config) {
        this.regionFactory = regionFactory;

        this.shields = shields;
        this.config = config;
    }


    @Override
    public ProtectedRegion create(Clan clan, ClanHome home) {
        ProtectedRegion region = this.regionFactory.create(clan, home);
        if(!shields.shield(clan.tag()).expired()) {
            region.setFlag(WgExtension.SHIELD_ACTIVE, true);
            this.config.shields().shieldFlags().apply(region);
        }
        return region;
    }

    @Override
    public void remove(Clan clan, ClanHome home) {
        this.regionFactory.remove(clan, home);
    }

    @Override
    public WgHome home(Clan clan, ClanHome home) {
        return this.regionFactory.home(clan, home);
    }
}
