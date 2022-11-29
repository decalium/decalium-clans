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

import org.gepron1x.clans.plugin.util.message.Message;
import space.arim.dazzleconf.annote.ConfKey;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import static space.arim.dazzleconf.annote.ConfDefault.DefaultString;

public interface PricesConfig {

    @DefaultDouble(50)
    @ConfKey("home-creation")
    double homeCreation();

    @DefaultDouble(50)
    @ConfKey("home-upgrade")
    double homeUpgrade();

    @DefaultDouble(100)
    @ConfKey("clan-creation")
    double clanCreation();

    @DefaultDouble(200)
    @ConfKey("clan-upgrade")
    double clanUpgrade();

    @DefaultDouble(1000)
    double shield();

    @DefaultString("<prefix><red> You need at least <price> to do that.")
    @ConfKey("not-enough-money")
    Message notEnoughMoney();


}
