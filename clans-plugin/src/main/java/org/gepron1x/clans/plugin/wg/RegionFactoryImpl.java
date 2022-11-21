/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;

import java.util.Optional;

public final class RegionFactoryImpl implements RegionFactory {

    private final WorldGuard worldGuard;
    private final ClansConfig config;

    public RegionFactoryImpl(WorldGuard worldGuard, ClansConfig config) {
        this.worldGuard = worldGuard;
        this.config = config;
    }


    @Override
    public ProtectedRegion create(Clan clan, ClanHome home) {
        Location location = home.location();
        double s = this.config.homes().homeRegionRadius();
        double lvl = home.level() + 1;
        double halfSize = Math.pow(1 + this.config.homes().levelRegionScale(), lvl) * s;
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        BlockVector3 first = BlockVector3.at(x - halfSize, y - halfSize, z - halfSize);
        BlockVector3 second = BlockVector3.at(x + halfSize, y + halfSize, z + halfSize);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(new NameForRegion(clan, home).value(), first, second);
        region.setFlag(WgExtension.CLAN, clan.tag());
        region.setFlag(WgExtension.HOME_NAME, home.name());
        this.config.homes().worldGuardFlags().apply(region);
        DefaultDomain members = region.getMembers();
        clan.memberMap().keySet().forEach(members::addPlayer);
        regionManager(location).orElseThrow().addRegion(region);
        return region;
    }

    @Override
    public void remove(Clan clan, ClanHome home) {
        regionManager(home.location()).ifPresent(mgr -> mgr.removeRegion(new NameForRegion(clan, home).value()));
    }

    @Override
    public WgHome home(Clan clan, ClanHome home) {
        return new WgHome(worldGuard, clan, home);
    }

    private Optional<RegionManager> regionManager(Location location) {
        return Optional.ofNullable(location.getWorld()).map(BukkitAdapter::adapt)
                .map(WorldGuard.getInstance().getPlatform().getRegionContainer()::get);
    }

}
