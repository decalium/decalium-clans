package org.gepron1x.clans.plugin.clan;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public final class ClanImpl extends AbstractClanBase implements Clan {
    private final int id;

    ClanImpl(int id, String tag,
                       Component displayName,
                       UUID owner, Map<UUID, ClanMember> memberMap,
                       Map<String, ClanHome> homeMap,
                       Map<StatisticType, Integer> statistics) {
        super(tag, displayName, owner, memberMap, homeMap, statistics);
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull DraftClan toDraft() {
        return new DraftClanImpl(getTag(), getDisplayName(), getOwner(), memberMap(), homeMap(), getStatistics());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClanImpl clan = (ClanImpl) o;
        return id == clan.id;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = (31 * result) + id;
        return id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("super", super.toString()).toString();
    }
}
