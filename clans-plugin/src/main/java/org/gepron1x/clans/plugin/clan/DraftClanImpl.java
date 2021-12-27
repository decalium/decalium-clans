package org.gepron1x.clans.plugin.clan;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public final class DraftClanImpl extends AbstractClanBase implements DraftClan {


    DraftClanImpl(String tag,
                            Component displayName,
                            UUID owner,
                            Map<UUID, ClanMember> memberMap,
                            Map<String, ClanHome> homeMap,
                            Map<StatisticType, Integer> statistics) {
        super(tag, displayName, owner, memberMap, homeMap, statistics);
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

    public static BuilderImpl builder() {
        return new BuilderImpl();
    }

    @Override
    public @NotNull DraftClan.Builder toBuilder() {
        return builder().tag(getTag())
                .owner(getOwner())
                .displayName(getDisplayName())
                .members(getMembers())
                .homes(getHomes())
                .statistics(getStatistics());
    }

    public static class BuilderImpl extends AbstractClanBase.Builder<DraftClan.Builder, DraftClan> implements DraftClan.Builder {


        @Override
        public @NotNull DraftClanImpl build() {
            return new DraftClanImpl(
                    tag,
                    displayName,
                    owner,
                    Map.copyOf(members),
                    Map.copyOf(homes),
                    Map.copyOf(statistics)
            );
        }

        @Override
        public BuilderImpl self() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o) && o.getClass() == this.getClass();
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("super", super.toString()).toString();
        }
    }
}
