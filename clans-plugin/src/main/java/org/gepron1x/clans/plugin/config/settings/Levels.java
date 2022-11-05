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
package org.gepron1x.clans.plugin.config.settings;

import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.Map;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import static space.arim.dazzleconf.annote.ConfDefault.DefaultMap;

public interface Levels {

    @ConfKey("allow-at")
    @SubSection AllowAt allowAt();

     interface AllowAt {

         @DefaultInteger(1)
         int wars();

         @DefaultInteger(3)
         int shields();
     }

     @ConfKey("per-level")
     @SubSection PerLevel perLevel();

    interface PerLevel {

        @DefaultInteger(5)
        int slots();

        @DefaultInteger(1)
        int homes();

        int shieldLevel();
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
        public int shieldLevel() {
            return per.shieldLevel() * level;
        }
    }

    @DefaultMap({})
    Map<Integer, @SubSection PerLevel> individual();


    default PerLevel forLevel(int level) {
        PerLevel per = individual().get(level);
        if(per != null) return per;
        return new AlgebraicPerLevel(perLevel(), level);

    }

}
