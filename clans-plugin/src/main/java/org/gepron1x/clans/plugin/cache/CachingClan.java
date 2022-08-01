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
package org.gepron1x.clans.plugin.cache;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Objects;
import java.util.function.Consumer;

public final class CachingClan implements Clan, DelegatingClan {

    private final Clan delegate;
    private final ClanCache cache;

    public CachingClan(Clan delegate, ClanCache cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> transaction) {
        return this.delegate.edit(transaction).thenApply(clan -> {
            if(cache.isCached(clan.tag())) {
                cache.removeClan(clan.tag());
                cache.cacheClan(clan);
            }
            return new CachingClan(clan, cache);
        });
    }

    @Override
    public int id() {
        return delegate.id();
    }

    @Override
    public DraftClan delegate() {
        return this.delegate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CachingClan that = (CachingClan) o;
        return delegate.equals(that.delegate) && cache.equals(that.cache);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, cache);
    }
}
