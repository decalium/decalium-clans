package org.gepron1x.clans.event.home;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.event.clan.ClanEvent;
import org.jetbrains.annotations.NotNull;

public class ClanAddHomeEvent extends ClanEvent implements HomeEvent, Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final ClanHome home;
    private boolean cancelled;

    public ClanAddHomeEvent(@NotNull Clan clan, @NotNull ClanHome home) {
        super(clan);
        this.home = home;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public ClanHome getHome() {
        return home;
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
