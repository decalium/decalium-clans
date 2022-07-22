/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.chat.common;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.util.ClanOnlinePlayers;
import org.gepron1x.clans.plugin.chat.resolvers.PapiTagResolver;
import org.gepron1x.clans.plugin.config.ClansConfig;

import java.util.Collections;
import java.util.Set;

public final class ClanChannel implements Channel {
    private final CachingClanRepository repository;
    private final Server server;
    private final ClansConfig config;

    public ClanChannel(CachingClanRepository repository, Server server, ClansConfig config) {

        this.repository = repository;
        this.server = server;
        this.config = config;
    }
    @Override
    public Component render(Player sender, Audience recipient, Component message, Component originalMessage) {
        Clan clan = this.repository.userClanIfCached(sender).orElseThrow();
        ClanMember member = clan.member(sender).orElseThrow();
        return config.chat().format()
                .with(new PapiTagResolver(sender))
                .with("clan", ClanTagResolver.clan(clan))
                .with("role", member.role())
                .with("member", sender.displayName())
                .with("message", originalMessage)
                .asComponent();
    }

    @Override
    public String prefix() {
        return config.chat().prefix();
    }

    @Override
    public boolean usePermitted(Player player) {
        return repository.userClanIfCached(player).isPresent();
    }

    @Override
    public Set<? extends Audience> recipients(Player player) {
        return this.repository.userClanIfCached(player).map(clan -> new ClanOnlinePlayers(clan, server).players())
                .map(Set::copyOf).orElse(Collections.emptySet());
    }

}
