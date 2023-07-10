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
package org.gepron1x.clans.plugin.war.navigation;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@SuppressWarnings("UnstableApiUsage")
public final class NavigationArrows {

	private final RangeMap<Double, String> arrows;

	public NavigationArrows(RangeMap<Double, String> arrows) {
		this.arrows = arrows;
	}

	public static NavigationArrows create(String arrows) {

		return new NavigationArrows(
				ImmutableRangeMap.<Double, String>builder()
						.put(Range.closed(0.0, 22.5), String.valueOf(arrows.charAt(0)))
						.put(Range.closed(22.51, 67.5), String.valueOf(arrows.charAt(1)))
						.put(Range.closed(67.51, 112.5), String.valueOf(arrows.charAt(2)))
						.put(Range.closed(112.51, 157.5), String.valueOf(arrows.charAt(3)))
						.put(Range.closed(157.51, 202.5), String.valueOf(arrows.charAt(4)))
						.put(Range.closed(202.51, 247.5), String.valueOf(arrows.charAt(5)))
						.put(Range.closed(247.51, 292.5), String.valueOf(arrows.charAt(6)))
						.put(Range.closed(292.51, 337.5), String.valueOf(arrows.charAt(7)))
						.put(Range.closed(337.51, 360.0), String.valueOf(arrows.charAt(0))).build()
		);
	}


	public String arrow(Location first, Location second) {
		Location snd = second.clone();
		Location fst = first.clone();
		snd.setY(0);
		fst.setY(0);
		fst.setPitch(0);
		Vector target = snd.subtract(fst).toVector().normalize();
		Vector look = fst.getDirection().normalize();

		double dot = look.dot(target);
		double det = look.getX() * target.getZ() - look.getZ() * target.getX();
		double angle = Math.toDegrees(Math.atan2(det, dot)) % 360;
		if (angle < 0) {
			angle += 360.0;
		}

		return Strings.nullToEmpty(arrows.get(angle));
	}
}
