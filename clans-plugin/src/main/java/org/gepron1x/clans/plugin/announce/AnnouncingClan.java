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
package org.gepron1x.clans.plugin.announce;

import com.google.common.base.MoreObjects;
import org.bukkit.Server;
import org.gepron1x.clans.api.audience.ClanAudience;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Objects;
import java.util.function.Consumer;

public final class AnnouncingClan implements Clan, DelegatingClan {

    private final Clan delegate;
    private transient final MessagesConfig messages;
    private transient final Server server;

    public AnnouncingClan(Clan delegate, MessagesConfig messages, Server server) {
        this.delegate = delegate;
        this.messages = messages;
        this.server = server;
    }

    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> transaction) {
        return this.delegate.edit(transaction).thenApplySync(clan -> {
            transaction.accept(new AnnouncingClanEdition(clan, new ClanAudience(clan, this.server), this.server, this.messages));
            return new AnnouncingClan(clan, this.messages, this.server);
        });
    }

    @Override
    public int id() {
        return this.delegate.id();
    }

    @Override
    public DraftClan delegate() {
        return this.delegate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnouncingClan that = (AnnouncingClan) o;
        return delegate.equals(that.delegate) && messages.equals(that.messages) && server.equals(that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, messages, server);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("delegate", delegate)
                .add("server", server)
                .toString();
    }
}
