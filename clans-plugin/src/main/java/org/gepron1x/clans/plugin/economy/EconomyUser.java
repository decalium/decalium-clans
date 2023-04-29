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

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.exception.DescribingException;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.plugin.config.settings.PricesConfig;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Optional;

public final class EconomyUser implements ClanUser {
    private final ClanUser user;
    private final VaultPlayer player;
    private final PricesConfig prices;
    private final FactoryOfTheFuture futuresFactory;

    public EconomyUser(ClanUser user, VaultPlayer player, PricesConfig prices, FactoryOfTheFuture futuresFactory) {
        this.user = user;
        this.player = player;
        this.prices = prices;
        this.futuresFactory = futuresFactory;
    }


	@Override
	public Optional<ClanRegions> regions() {
		return user.regions();
	}

	@Override
    public Optional<Clan> clan() {
        return user.clan().map(clan -> new EconomyClan(clan, prices, player, futuresFactory));
    }

    @Override
    public CentralisedFuture<ClanCreationResult> create(DraftClan draft) {
        if(!player.has(prices.clanCreation())) return futuresFactory.failedFuture(new DescribingException(prices.notEnoughMoney().with("amount", prices.clanCreation()).asComponent()));
        return user.create(draft);
    }

    @Override
    public CentralisedFuture<Boolean> delete() {
        return user.delete();
    }
}
