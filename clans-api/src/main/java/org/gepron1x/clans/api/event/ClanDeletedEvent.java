package org.gepron1x.clans.api.event;

import org.bukkit.event.HandlerList;
import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;

public final class ClanDeletedEvent extends ClanEvent {

    private static final HandlerList handlers = new HandlerList();

    public ClanDeletedEvent(Clan clan) {
        super(clan);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
