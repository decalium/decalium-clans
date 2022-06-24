package org.gepron1x.clans.api.event;

import org.bukkit.event.Event;
import org.gepron1x.clans.api.clan.Clan;

public abstract class ClanEvent extends Event {
    protected final Clan clan;

    public ClanEvent(Clan clan) {

        this.clan = clan;
    }

    public Clan clan() {
        return clan;
    }
}
