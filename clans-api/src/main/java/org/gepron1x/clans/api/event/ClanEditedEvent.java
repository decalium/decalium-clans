package org.gepron1x.clans.api.event;

import org.bukkit.event.HandlerList;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClanEditedEvent extends ClanEvent {

    private static final HandlerList handlers = new HandlerList();
    private final @Nullable IdentifiedDraftClan result;

    public ClanEditedEvent(@Nullable IdentifiedDraftClan clan, @Nullable IdentifiedDraftClan result) {
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

    public @Nullable IdentifiedDraftClan getResult() {
        return result;
    }
}
