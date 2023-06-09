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
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.EmptyClanEdition;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.function.Consumer;

public final class CachingClan implements Clan, DelegatingClan {

    private volatile Clan delegate;
	private final ClanCache cache;
	private final FactoryOfTheFuture futuresFactory;

	private CentralisedFuture<Clan> currentEdition;

    public CachingClan(Clan delegate, ClanCache cache, FactoryOfTheFuture futuresFactory) {
        this.delegate = delegate;
		this.cache = cache;
		this.currentEdition = futuresFactory.completedFuture(delegate);
		this.futuresFactory = futuresFactory;
	}

    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> transaction) {
		if(currentEdition.isCompletedExceptionally()) currentEdition = futuresFactory.completedFuture(delegate);
		this.currentEdition = currentEdition.thenCompose(clan -> clan.edit(transaction));
        return currentEdition.thenApply(clan -> {
			transaction.accept(new EmptyClanEdition() {
				@Override
				public ClanEdition addMember(@NotNull ClanMember member) {
					cache.cacheClan(member.uniqueId(), CachingClan.this);
					return this;
				}

				@Override
				public ClanEdition removeMember(@NotNull ClanMember member) {
					cache.removeClanEntry(member.uniqueId());
					return this;
				}
			});
			synchronized(CachingClan.this) {
				this.delegate = clan;
			}
			return this;
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
        return delegate.equals(that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }
}
