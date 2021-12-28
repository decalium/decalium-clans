package org.gepron1x.clans.api.event;

import org.bukkit.event.Event;
import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;

public abstract class ClanEvent extends Event {
    protected final Clan clan;

    public ClanEvent(@NotNull Clan clan) {

        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }
}
