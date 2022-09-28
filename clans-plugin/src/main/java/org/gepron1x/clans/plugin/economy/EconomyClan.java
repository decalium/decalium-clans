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

import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.AtomicDouble;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.exception.DescribingException;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.gepron1x.clans.plugin.config.PricesConfig;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.function.Consumer;

public final class EconomyClan implements Clan, DelegatingClan {

    private final Clan clan;
    private final PricesConfig prices;
    private final VaultPlayer player;
    private final FactoryOfTheFuture futuresFactory;

    public EconomyClan(Clan clan, PricesConfig prices, VaultPlayer player, FactoryOfTheFuture futuresFactory) {

        this.clan = clan;
        this.prices = prices;
        this.player = player;
        this.futuresFactory = futuresFactory;
    }
    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> transaction) {
        AtomicDouble cost = new AtomicDouble(0);
        transaction.accept(new EconomyEdition(cost, prices));
        if(!player.has(cost.get())) {
            return futuresFactory.failedFuture(new DescribingException(prices.notEnoughMoney().with("price", cost.get()).asComponent()));
        }
        player.withdraw(cost.get());
        return clan.edit(transaction);
    }

    @Override
    public int id() {
        return clan.id();
    }

    @Override
    public DraftClan delegate() {
        return this.clan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EconomyClan that = (EconomyClan) o;
        return clan.equals(that.clan) && player.equals(that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clan, player);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clan", clan)
                .add("player", player)
                .toString();
    }
}
