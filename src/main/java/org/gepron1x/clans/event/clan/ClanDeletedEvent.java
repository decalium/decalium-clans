package org.gepron1x.clans.event.clan;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.gepron1x.clans.clan.Clan;
import org.jetbrains.annotations.NotNull;

public class ClanDeletedEvent extends ClanEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    public ClanDeletedEvent(@NotNull Clan clan) {
        super(clan);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
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
