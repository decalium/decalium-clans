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
package org.gepron1x.clans.plugin.economy;

import com.google.common.util.concurrent.AtomicDouble;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.EmptyClanEdition;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.plugin.config.settings.PricesConfig;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class EconomyEdition implements EmptyClanEdition {

	private final AtomicDouble cost;
	private final PricesConfig prices;
	private final Clan clan;

	public EconomyEdition(AtomicDouble cost, PricesConfig prices, Clan clan) {

		this.cost = cost;
		this.prices = prices;
		this.clan = clan;
	}

	@Override
	public ClanEdition addHome(@NotNull ClanHome home) {
		return pay(prices.homeCreation());
	}

	@Override
	public ClanEdition upgrade() {
		return pay(prices.clanUpgrade(clan.level() + 1));
	}

	@Override
	public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
		consumer.accept(new EconomyHomeEdition());
		return this;
	}

	public ClanEdition pay(double amount) {
		this.cost.addAndGet(amount);
		return this;
	}

	private static final class EconomyHomeEdition implements EmptyHomeEdition {

		@Override
		public HomeEdition upgrade() {
			return this;
		}
	}
}
