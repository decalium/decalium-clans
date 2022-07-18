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
package org.gepron1x.clans.api.util;

import com.google.common.base.MoreObjects;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ClanOnlinePlayers implements Iterable<Player> {

    private final DraftClan clan;
    private final Server server;

    public  ClanOnlinePlayers(DraftClan clan, Server server) {

        this.clan = clan;
        this.server = server;
    }

    public Collection<Player> players() {
        Set<Player> players = new HashSet<>(clan.members().size());
        for(ClanMember member : clan.members()) {
            Player player = member.asPlayer(this.server);
            if(player != null) players.add(player);
        }
        return Set.copyOf(players);
    }

    @NotNull
    @Override
    public Iterator<Player> iterator() {
        return players().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanOnlinePlayers players = (ClanOnlinePlayers) o;
        return clan.equals(players.clan) && server.equals(players.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clan, server);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clan", clan)
                .add("server", server)
                .toString();
    }

}
