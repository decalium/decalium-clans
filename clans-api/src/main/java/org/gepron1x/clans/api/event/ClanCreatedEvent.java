package org.gepron1x.clans.api.event;

import org.bukkit.event.HandlerList;
import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;

public final class ClanCreatedEvent extends ClanEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ClanCreatedEvent(@NotNull Clan clan) {
        super(clan);
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
