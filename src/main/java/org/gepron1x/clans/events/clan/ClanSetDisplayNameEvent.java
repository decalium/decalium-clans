package org.gepron1x.clans.events.clan;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.gepron1x.clans.clan.Clan;
import org.jetbrains.annotations.NotNull;

public class ClanSetDisplayNameEvent extends ClanEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private Component newDisplayName;
    private boolean cancelled = false;

    public ClanSetDisplayNameEvent(@NotNull Clan clan, @NotNull Component newDisplayName) {
        super(clan);
        this.newDisplayName = newDisplayName;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Component getNewDisplayName() {
        return newDisplayName;
    }
    public void setNewDisplayName(Component displayName) {
        this.newDisplayName = displayName;
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
