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
package org.gepron1x.clans.plugin.war.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.util.ClanOnlinePlayers;
import org.gepron1x.clans.api.util.player.PlayerReference;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.api.war.War;
import org.gepron1x.clans.api.war.Wars;

import java.util.*;
import java.util.stream.Collectors;

public final class DefaultWars implements Wars {


    private final Set<War> currentWars = new HashSet<>();
    private final Server server;

    public DefaultWars(Server server) {

        this.server = server;
    }

    @Override
    public void start(War war) {
        Preconditions.checkArgument(!this.currentWars.contains(war), "war already started");
        this.currentWars.add(war);
    }

    @Override
    public War create(Team first, Team second) {
        Preconditions.checkState(first.clan().cached().isPresent() && second.clan().cached().isPresent(), "no online players in some of clan!");
        Preconditions.checkState(!first.clan().equals(second.clan()), "Cannot start a war against itself");
        return new DefaultWar(
                List.of(first, second)
        );
    }

    @Override
    public Team createTeam(ClanReference ref) {
        return createFromClan(ref);
    }

    private DefaultTeam createFromClan(ClanReference reference) {
        Clan clan = reference.cached().orElseThrow();
        return new DefaultTeam(
                reference,
                new ClanOnlinePlayers(clan, this.server).players()
                        .stream()
                        .map(PlayerReference::reference)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public Optional<War> currentWar(Player player) {
        for(War war : this.currentWars) {
            if(war.team(player).isPresent()) return Optional.of(war);
        }
        return Optional.empty();
    }

    @Override
    public Collection<War> currentWars() {
        return Collections.unmodifiableCollection(this.currentWars);
    }

    @Override
    public void onDeath(Player player) {
        Iterator<War> iterator = this.currentWars.iterator();
        while(iterator.hasNext()) {
            War war = iterator.next();
            if(!war.onPlayerDeath(player)) continue;
            if(war.isEnded()) iterator.remove();
        }

    }

    @Override
    public void end(War war) {
        this.currentWars.remove(war);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultWars that = (DefaultWars) o;
        return currentWars.equals(that.currentWars) && server.equals(that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentWars, server);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("currentWars", currentWars)
                .toString();
    }

    @Override
    public void cleanEnded() {
        this.currentWars.removeIf(War::isEnded);
    }

}
