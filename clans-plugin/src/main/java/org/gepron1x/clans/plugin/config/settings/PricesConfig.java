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

import org.gepron1x.clans.api.chat.action.Message;
import org.gepron1x.clans.api.economy.Prices;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

import java.util.Map;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import static space.arim.dazzleconf.annote.ConfDefault.DefaultString;

public interface PricesConfig extends Prices {

    @DefaultDouble(800)
    @ConfKey("home-creation")
	@Override
    double homeCreation();

    @DefaultDouble(100)
    @ConfKey("clan-creation")
	@Override
    double clanCreation();

    @DefaultDouble(200)
    @ConfKey("clan-upgrade")
    double clanUpgrade();

	@ConfDefault.DefaultMap({})
	@ConfKey("upgrade-prices")
	Map<Integer, Double> upgradePrices();

	@Override
	default double clanUpgrade(int level) {
		Double value = upgradePrices().get(level);
		return value == null ? clanUpgrade() * (Math.pow(2, level)) / 2 : value;
	}

	@Override
	@ConfKey("region")
	@DefaultDouble(500)
	double region();

	@DefaultDouble(1000)
	@Override
    double shield();

    @DefaultString("<prefix><red> You need at least <price> to do that.")
    @ConfKey("not-enough-money")
	Message notEnoughMoney();


}
