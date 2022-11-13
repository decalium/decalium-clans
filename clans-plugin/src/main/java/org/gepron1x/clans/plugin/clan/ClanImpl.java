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
package org.gepron1x.clans.plugin.clan;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.function.Consumer;

public final class ClanImpl implements Clan, DelegatingClan {

    private final int id;
    private final DraftClan draftClan;
    private transient final ClanStorage storage;
    private transient final FactoryOfTheFuture futuresFactory;

    public ClanImpl(int id, DraftClan draftClan, ClanStorage storage, FactoryOfTheFuture futuresFactory) {

        this.id = id;
        this.draftClan = draftClan;
        this.storage = storage;
        this.futuresFactory = futuresFactory;
    }
    @Override
    public int id() {
        return id;
    }

    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> transaction) {
        DraftClan.Builder builder = draftClan.toBuilder();
        builder.applyEdition(transaction);
        DraftClan clan = builder.build();
        return futuresFactory.runAsync(() -> this.storage.applyEdition(this.id, transaction))
                .thenApply(ignored -> new ClanImpl(this.id, clan, this.storage, this.futuresFactory));
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanImpl clan2 = (ClanImpl) o;
        return id == clan2.id && draftClan.equals(clan2.draftClan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, draftClan);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("draftClan", draftClan)
                .add("storage", storage)
                .add("futuresFactory", futuresFactory)
                .toString();
    }

    @Override
    public DraftClan delegate() {
        return this.draftClan;
    }
}
