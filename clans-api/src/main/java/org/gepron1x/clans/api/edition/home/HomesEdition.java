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
package org.gepron1x.clans.api.edition.home;

import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.home.Homes;
import org.gepron1x.clans.api.edition.RegistryEdition;

import java.util.Collection;
import java.util.function.Consumer;

public interface HomesEdition extends RegistryEdition<String, ClanHome, HomeEdition, Homes> {

    @Override
    HomesEdition add(ClanHome value);

    @Override
    HomesEdition add(Collection<ClanHome> values);

    @Override
    HomesEdition remove(String key);

    @Override
    HomesEdition edit(String key, Consumer<HomeEdition> consumer);
}
