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
package org.gepron1x.clans.plugin.config.settings;

import org.gepron1x.clans.api.economy.LevelsMeta;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.time.Duration;
import java.util.Map;

import static space.arim.dazzleconf.annote.ConfDefault.*;

public interface Levels extends LevelsMeta {

    @DefaultBoolean(true)
    boolean enabled();

	@Override
    @DefaultInteger(10)
    @ConfKey("max-level")
    int maxLevel();

	@Override
    @ConfKey("allow-at")
    @SubSection AllowAt allowAt();

     interface AllowAt extends LevelsMeta.AllowAt {

		 @Override
         @DefaultInteger(1)
         int wars();

		 @Override
         @DefaultInteger(3)
         int regions();

		 @Override
		 @DefaultInteger(5)
		 int homes();
		 @Override
		 @DefaultInteger(4)
		 @ConfKey("region-effects")
		 int regionEffects();

		 @Override
		 @DefaultInteger(3)
		 int shields();

		 @Override
		 @DefaultInteger(1)
		 int colors();

		 @Override
		 @DefaultInteger(5)
		 int gradients();

		 @Override
		 @DefaultInteger(5)
		 int symbols();
	 }

     @ConfKey("per-level")
     @SubSection PerLevel perLevel();

    interface PerLevel extends LevelsMeta.PerLevel {
		@Override
        @DefaultInteger(5)
        int slots();

		@Override
        @DefaultInteger(1)
        int homes();

		@Override
		@DefaultInteger(1)
		int regions();

		@Override
        @ConfKey("shield-duration")
        @DefaultString("2h")
        Duration shieldDuration();
    }

    class AlgebraicPerLevel implements PerLevel {

        private final PerLevel per;
        private final int level;

        public AlgebraicPerLevel(PerLevel per, int level) {

            this.per = per;
            this.level = level;
        }

        @Override
        public int slots() {
            return per.slots() * level;
        }

        @Override
        public int homes() {
            return per.slots() * level;
        }

		@Override
		public int regions() {
			return per.regions() * level;
		}

		@Override
        public Duration shieldDuration() {
            return per.shieldDuration().multipliedBy(level);
        }

    }

    @DefaultMap({})
    Map<Integer, @SubSection PerLevel> individual();
	@Override
    default PerLevel forLevel(int level) {
        PerLevel per = individual().get(level);
        if(per != null) return per;
        return new AlgebraicPerLevel(perLevel(), level);

    }


}
