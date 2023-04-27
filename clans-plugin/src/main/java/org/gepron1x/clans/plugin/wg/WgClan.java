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
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.edition.PostClanEdition;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class WgClan implements DelegatingClan, Clan {
    private final Clan delegate;
    private final Configs configs;
	private final RegionContainer container;
	private final ClanRegions regions;
	private final FactoryOfTheFuture futuresFactory;

	public WgClan(Clan delegate, Configs configs, RegionContainer container, ClanRegions regions, FactoryOfTheFuture futuresFactory) {
        this.delegate = delegate;
        this.configs = configs;
		this.container = container;
		this.regions = regions;
		this.futuresFactory = futuresFactory;
	}


    @Override
    public DraftClan delegate() {
        return this.delegate;
    }


    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> transaction) {
		CentralisedFuture<Clan> edition = this.delegate.edit(transaction);
		CentralisedFuture<Set<ClanRegion>> regionSet = this.regions.regions();
		return futuresFactory.allOf(edition, regionSet).thenApply($ -> {
			Clan newClan = edition.join();
			transaction.accept(new PostClanEdition(newClan, configs.config(), new WgRegionSet(container, regionSet.join())));
			return new WgClan(newClan, configs, container, regions, futuresFactory);
		});
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WgClan wgClan = (WgClan) o;
        return delegate.equals(wgClan.delegate) && configs.equals(wgClan.configs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, configs);
    }

    @Override
    public int id() {
        return this.delegate.id();
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("delegate", delegate)
                .toString();
    }
}
