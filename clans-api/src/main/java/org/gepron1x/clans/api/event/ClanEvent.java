package org.gepron1x.clans.api.event;

import org.bukkit.event.Event;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.jetbrains.annotations.Nullable;

public abstract class ClanEvent extends Event {
    protected final @Nullable IdentifiedDraftClan clan;

    public ClanEvent(@Nullable IdentifiedDraftClan clan) {

        this.clan = clan;
    }

    public @Nullable IdentifiedDraftClan getClan() {
        return clan;
    }
}
