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

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.plugin.AdaptingClanRepository;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.gepron1x.clans.plugin.edition.PostClanEdition;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

public class WgRepositoryImpl extends AdaptingClanRepository {

    private final ClansConfig clansConfig;
    private final RegionFactory regionFactory;

    public WgRepositoryImpl(ClanRepository repository, ClansConfig clansConfig, RegionFactory regionFactory) {
        super(repository, clan -> new WgClan(clan, clansConfig, regionFactory));
        this.clansConfig = clansConfig;
        this.regionFactory = regionFactory;
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return super.removeClan(clan).thenApplySync(bool -> {
            if(!bool) return false;
            PostClanEdition postClanEdition = new PostClanEdition(clan, this.clansConfig, this.regionFactory);
            clan.homes().forEach(postClanEdition::removeHome);
            return true;
        });
    }
}
