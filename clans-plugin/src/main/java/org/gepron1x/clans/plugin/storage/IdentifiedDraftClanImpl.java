package org.gepron1x.clans.plugin.storage;

import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.plugin.clan.DelegatingClan;

public record IdentifiedDraftClanImpl(int id, DraftClan clan) implements IdentifiedDraftClan, DelegatingClan {

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public DraftClan delegate() {
        return this.clan;
    }
}
