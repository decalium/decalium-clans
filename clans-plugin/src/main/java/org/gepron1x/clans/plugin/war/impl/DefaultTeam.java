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
package org.gepron1x.clans.plugin.war.impl;

import com.google.common.base.MoreObjects;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.util.player.PlayerReference;
import org.gepron1x.clans.api.war.Team;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

public final class DefaultTeam implements Team {
    private final ClanReference clan;
    private final Collection<PlayerReference> members;
    private final Collection<PlayerReference> alive;

    public DefaultTeam(ClanReference clan, Collection<PlayerReference> members) {
        this.clan = clan;
        this.members = members;
        this.alive = new HashSet<>(members);
    }

    @Override
    public ClanReference clan() {
        return this.clan;
    }

    @Override
    public Collection<PlayerReference> members() {
        return this.members;
    }

    @Override
    public Collection<PlayerReference> alive() {
        return Collections.unmodifiableCollection(this.alive);
    }

    @Override
    public boolean onDeath(Player player) {
        return alive.removeIf(ref -> ref.player().map(p -> p.equals(player)).orElse(false));
    }

    @Override
    public boolean isAlive() {
        return !this.alive.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultTeam that = (DefaultTeam) o;
        return clan.equals(that.clan) && members.equals(that.members) && alive.equals(that.alive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clan, members, alive);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clan", clan)
                .add("members", members)
                .add("alive", alive)
                .toString();
    }
}
