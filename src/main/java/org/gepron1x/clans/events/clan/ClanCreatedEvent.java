package org.gepron1x.clans.events.clan;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.gepron1x.clans.clan.Clan;
import org.jetbrains.annotations.NotNull;

public class ClanCreatedEvent extends ClanEvent implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();


    private boolean cancelled = false;

    public ClanCreatedEvent(@NotNull Clan clan) {
        super(clan);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
