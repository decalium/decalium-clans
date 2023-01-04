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

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;

public record NameForRegion(Clan clan, ClanHome home) {


    public String value() {
        String fst = clan.tag();
        if(!ProtectedRegion.isValidId(fst)) fst = Integer.toHexString(clan.id());

        String snd = home.name();
        if(ProtectedRegion.isValidId(snd)) {
            Location loc = home.location();
            snd = "X" + hex(loc.getBlockX()) + "Y" + hex(loc.getBlockY()) + "Z" + hex(loc.getBlockZ());
        }

        return "decaliumclans_"+fst+"_"+snd;
    }

    private String hex(int v) {
        return Integer.toHexString(v);
    }

}
