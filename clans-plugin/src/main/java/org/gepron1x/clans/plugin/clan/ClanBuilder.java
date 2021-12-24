package org.gepron1x.clans.plugin.clan;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanBase;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ClanBuilder extends AbstractClanBase.Builder<ClanBuilder, Clan> {

    private int id;


    public static ClanBuilder asBuilder(@NotNull ClanBase clan, int id) {
        return new ClanBuilder(id)
                .tag(clan.getTag())
                .displayName(clan.getDisplayName())
                .owner(clan.getOwner())
                .members(clan.getMembers())
                .homes(clan.getHomes())
                .statistics(clan.getStatistics());
    }

    public static ClanBuilder asBuilder(@NotNull Clan clan) {
        return asBuilder(clan, clan.getId());
    }

    public ClanBuilder(int id) {
        this.id = id;
    }


    @Override
    public ClanBuilder self() {
        return this;
    }

    @Override
    public @NotNull Clan build() {
        return new ClanImpl(id, tag, displayName, owner,
                Map.copyOf(members),
                Map.copyOf(homes),
                Map.copyOf(statistics)
        );
    }
}
