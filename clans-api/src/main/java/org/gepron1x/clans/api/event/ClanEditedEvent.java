package org.gepron1x.clans.api.event;

import org.bukkit.event.HandlerList;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClanEditedEvent extends ClanEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Clan result;

    public ClanEditedEvent(Clan clan, Clan result) {
        super(clan);
        this.result = result;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public @Nullable IdentifiedDraftClan result() {
        return result;
    }
}
