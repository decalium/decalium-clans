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
package org.gepron1x.clans.api.util.player;

import com.google.common.base.MoreObjects;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public final class UuidPlayerReference implements PlayerReference {

    private final Server server;
    private final UUID uniqueId;

    public UuidPlayerReference(Server server, UUID uniqueId) {

        this.server = server;
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean ifOnline(Consumer<Player> consumer) {
        Optional<Player> opt = player();
        opt.ifPresent(consumer);
        return opt.isPresent();
    }

    @Override
    public Optional<Player> player() {
        return Optional.ofNullable(this.server.getPlayer(this.uniqueId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UuidPlayerReference that = (UuidPlayerReference) o;
        return server.equals(that.server) && uniqueId.equals(that.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, uniqueId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("server", server)
                .add("uniqueId", uniqueId)
                .toString();
    }
}
