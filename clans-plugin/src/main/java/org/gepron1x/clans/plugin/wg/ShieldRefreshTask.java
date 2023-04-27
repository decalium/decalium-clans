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

import org.bukkit.scheduler.BukkitRunnable;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.GlobalRegions;

public final class ShieldRefreshTask extends BukkitRunnable {

	private final GlobalRegions regions;


    public ShieldRefreshTask(GlobalRegions regions) {
		this.regions = regions;
    }
    @Override
    public void run() {
       regions.listRegions().thenAccept(list -> {
		   for(ClanRegion region : list) {
			   if(region.shield().expired()) region.removeShield();
		   }
	   }).join();
    }

}
