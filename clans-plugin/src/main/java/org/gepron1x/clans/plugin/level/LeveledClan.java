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
package org.gepron1x.clans.plugin.level;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.exception.DescribingException;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.gepron1x.clans.plugin.config.Configs;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.function.Consumer;

public final class LeveledClan implements Clan, DelegatingClan {

	private final FactoryOfTheFuture futuresFactory;
	private final Configs configs;
	private final Clan clan;

	public LeveledClan(FactoryOfTheFuture futuresFactory, Configs configs, Clan clan) {
		this.futuresFactory = futuresFactory;
		this.configs = configs;
		this.clan = clan;
	}

	@Override
	public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> transaction) {
		try {
			transaction.accept(new LeveledEdition(clan, configs.config().levels().forLevel(clan.level()), configs));
		} catch (DescribingException ex) {
			return futuresFactory.failedFuture(ex);
		}
		return clan.edit(transaction).thenApply(c -> new LeveledClan(futuresFactory, configs, c));
	}

	@Override
	public int id() {
		return clan.id();
	}

	@Override
	public DraftClan delegate() {
		return clan;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LeveledClan that = (LeveledClan) o;
		return Objects.equals(clan, that.clan);
	}

	@Override
	public int hashCode() {
		return Objects.hash(configs, clan);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("clan", clan)
				.toString();
	}
}
