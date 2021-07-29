package org.gepron1x.clans.events;

import org.bukkit.event.Event;
import org.gepron1x.clans.clan.Clan;
import org.jetbrains.annotations.NotNull;

public abstract class ClanEvent extends Event {

    private final Clan clan;

    public ClanEvent(@NotNull Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }
}
