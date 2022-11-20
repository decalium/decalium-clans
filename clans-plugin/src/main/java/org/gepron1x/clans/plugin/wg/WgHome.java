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
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;

import java.util.Optional;

public record WgHome(WorldGuard worldGuard, Clan clan, ClanHome home) {


    public Optional<ProtectedRegion> region() {
        Location location = home.location();
        return Optional.ofNullable(location.getWorld()).map(BukkitAdapter::adapt)
                .map(worldGuard.getPlatform().getRegionContainer()::get)
                .map(mgr -> mgr.getRegion(new NameForRegion(clan, home).value()));
    }

    public boolean shieldActive() {
        return region().map(r -> r.getFlag(WgExtension.SHIELD_ACTIVE)).orElse(false);
    }

}
