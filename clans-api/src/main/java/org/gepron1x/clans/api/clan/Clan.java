package org.gepron1x.clans.api.clan;

import org.jetbrains.annotations.NotNull;

public interface Clan extends ClanBase {
    int getId();
    @NotNull DraftClan toDraft();
}
