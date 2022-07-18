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
package org.gepron1x.clans.plugin.command.parser;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.CachingClanRepository;

import java.util.Optional;

public final class ClanOfSender {

    private final CachingClanRepository repository;
    private final CommandSender sender;

    public ClanOfSender(CachingClanRepository repository, CommandSender sender) {

        this.repository = repository;
        this.sender = sender;
    }

    public Optional<Clan> clan() {
        return Optional.of(sender).filter(Player.class::isInstance)
                .map(Player.class::cast).flatMap(repository::userClanIfCached);
    }
}
