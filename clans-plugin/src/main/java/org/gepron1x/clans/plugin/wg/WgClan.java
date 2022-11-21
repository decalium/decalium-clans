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
package org.gepron1x.clans.plugin.wg;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.gepron1x.clans.plugin.edition.PostClanEdition;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Objects;
import java.util.function.Consumer;

public class WgClan implements DelegatingClan, Clan {
    private final Clan delegate;
    private final ClansConfig clansConfig;
    private final RegionFactory regionFactory;

    public WgClan(Clan delegate, ClansConfig clansConfig, RegionFactory regionFactory) {
        this.delegate = delegate;
        this.clansConfig = clansConfig;
        this.regionFactory = regionFactory;
    }


    @Override
    public DraftClan delegate() {
        return this.delegate;
    }


    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> transaction) {
        return this.delegate.edit(transaction).thenApplySync(clan -> {
            transaction.accept(new PostClanEdition(clan, this.clansConfig, regionFactory));
            return new WgClan(clan, this.clansConfig, this.regionFactory);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WgClan wgClan = (WgClan) o;
        return delegate.equals(wgClan.delegate) && clansConfig.equals(wgClan.clansConfig) && regionFactory.equals(wgClan.regionFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, clansConfig, regionFactory);
    }

    @Override
    public int id() {
        return this.delegate.id();
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("delegate", delegate)
                .add("regionFactory", regionFactory)
                .toString();
    }
}
