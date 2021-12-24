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

public class ClanImpl extends AbstractClanBase implements Clan {
    private final int id;

    protected ClanImpl(int id, String tag,
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
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("super", super.toString()).toString();
    }
}
