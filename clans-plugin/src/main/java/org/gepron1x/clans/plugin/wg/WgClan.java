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

import com.google.common.base.MoreObjects;
import com.sk89q.worldguard.WorldGuard;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.edition.PostClanEdition;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Objects;
import java.util.function.Consumer;

public class WgClan implements DelegatingClan, Clan {
    private final Clan delegate;
    private final Configs configs;
    private final RegionFactory regionFactory;
    private final WorldGuard worldGuard;

    public WgClan(Clan delegate, Configs configs, RegionFactory regionFactory, WorldGuard worldGuard) {
        this.delegate = delegate;
        this.configs = configs;
        this.regionFactory = regionFactory;
        this.worldGuard = worldGuard;
    }


    @Override
    public DraftClan delegate() {
        return this.delegate;
    }


    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> transaction) {
        return this.delegate.edit(transaction).thenApplySync(clan -> {
            transaction.accept(new PostClanEdition(clan, this.configs.config(), regionFactory));
            return new WgClan(clan, this.configs, this.regionFactory, this.worldGuard);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WgClan wgClan = (WgClan) o;
        return delegate.equals(wgClan.delegate) && configs.equals(wgClan.configs) && regionFactory.equals(wgClan.regionFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, configs, regionFactory);
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
