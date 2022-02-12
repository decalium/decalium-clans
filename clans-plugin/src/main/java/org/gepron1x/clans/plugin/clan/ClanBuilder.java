package org.gepron1x.clans.plugin.clan;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanBase;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

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

    public ClanBuilder id(int id) {
        this.id = id;
        return self();
    }
    @Contract("_ -> this")
    public ClanBuilder incrementStatistic(@NotNull StatisticType type) {
        this.statistics.mergeInt(type, 1, Integer::sum);
        return self();
    }
    @Contract("_ -> this")
    public ClanBuilder removeStatistic(@NotNull StatisticType type) {
        this.statistics.removeInt(type);
        return self();
    }

    public ClanMember member(@NotNull UUID uuid) {
        return this.members.get(uuid);
    }

    public ClanHome home(@NotNull String name) {
        return this.homes.get(name);
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

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = (31 * result) + id;
        return result;
    }



    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("super", super.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClanBuilder that = (ClanBuilder) o;
        return id == that.id;
    }
}
