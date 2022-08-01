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
package org.gepron1x.clans.api.event;

import org.bukkit.event.HandlerList;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.event.common.AbstractCancellable;
import org.gepron1x.clans.api.event.common.AbstractClanEvent;
import org.gepron1x.clans.api.event.common.Cancellation;
import org.jetbrains.annotations.NotNull;

public final class ClanDeleteEvent extends AbstractClanEvent implements AbstractCancellable {


    private static final HandlerList handlers = new HandlerList();

    private final Cancellation cancellation;

    public ClanDeleteEvent(Clan clan) {
        super(clan);
        cancellation = new Cancellation(false);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public Cancellation cancellation() {
        return cancellation;
    }
}
