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
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.event.common.AbstractClanEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class ClanEditionEvent extends AbstractClanEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Consumer<ClanEdition> transaction;

    public ClanEditionEvent(Clan clan, Consumer<ClanEdition> transaction) {
        super(clan);
        this.transaction = transaction;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Consumer<ClanEdition> transaction() {
        return transaction;
    }


}
