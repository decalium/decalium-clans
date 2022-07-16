package org.gepron1x.clans.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;

public final class ClanDeletedEvent extends ClanEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
