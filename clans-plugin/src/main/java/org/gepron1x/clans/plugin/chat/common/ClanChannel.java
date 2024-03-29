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
package org.gepron1x.clans.plugin.chat.common;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.util.ClanOnlinePlayers;
import org.gepron1x.clans.plugin.chat.resolvers.PapiTagResolver;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ClanChannel implements Channel {

	private static final Key KEY = Key.key("decaliumclans:chat");
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

	@Override
	public Set<Player> filter(Player sender, Set<Player> receivers) {
		return this.repository.userClanIfCached(sender).map(clan -> {
			Set<Player> filtered = new HashSet<>(receivers);
			filtered.removeIf(p -> clan.member(p).isEmpty());
			return filtered;
		}).map(Set::copyOf).orElse(Collections.emptySet());
	}

	@Override
	public Key key() {
		return KEY;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClanChannel that = (ClanChannel) o;
		return repository.equals(that.repository) && server.equals(that.server) && config.equals(that.config);
	}

	@Override
	public int hashCode() {
		return Objects.hash(repository, server, config);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("repository", repository)
				.add("server", server)
				.toString();
	}
}
