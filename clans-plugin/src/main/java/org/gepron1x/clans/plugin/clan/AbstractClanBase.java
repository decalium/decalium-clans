package org.gepron1x.clans.plugin.clan;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.ClanBase;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.util.FancyCollections;
import org.gepron1x.clans.plugin.util.Optionals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public abstract class AbstractClanBase implements ClanBase {


    private final String tag;
    private final Component displayName;
    private final ClanMember owner;
    private final Map<UUID, ClanMember> memberMap;
    private final Map<String, ClanHome> homeMap;
    private final Map<StatisticType, Integer> statistics;

    protected AbstractClanBase(String tag,
                               Component displayName,
                               ClanMember owner,
                               Map<UUID, ClanMember> memberMap,
                               Map<String, ClanHome> homeMap,
                               Map<StatisticType, Integer> statistics) {

        this.tag = tag;
        this.displayName = displayName;
        this.owner = owner;
        this.memberMap = memberMap;
        this.homeMap = homeMap;
        this.statistics = statistics;
    }

    @Override
    public @NotNull String getTag() {
        return tag;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return displayName;
    }

    @Override
    public @NotNull ClanMember getOwner() {
        return owner;
    }

    @Override
    public @NotNull @Unmodifiable Collection<ClanMember> getMembers() {
        return memberMap.values();
    }

    @Override
    public @NotNull @Unmodifiable Map<UUID, ClanMember> memberMap() {
        return memberMap;
    }

    @Override
    public @Nullable ClanMember getMember(@NotNull UUID uuid) {
        return memberMap.get(uuid);
    }


    @Override
    public @NotNull @Unmodifiable Collection<ClanHome> getHomes() {
        return homeMap.values();
    }

    @Override
    public @Nullable ClanHome getHome(@NotNull String name) {
        return homeMap.get(name);
    }

    @Override
    public @NotNull @Unmodifiable Map<String, ClanHome> homeMap() {
        return homeMap;
    }

    @Override
    public OptionalInt getStatistic(@NotNull StatisticType type) {
        return Optionals.ofNullable(statistics.get(type));
    }


    @Override
    public @NotNull @Unmodifiable Map<StatisticType, Integer> getStatistics() {
        return statistics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractClanBase that = (AbstractClanBase) o;
        return tag.equals(that.tag) &&
                displayName.equals(that.displayName) &&
                owner.equals(that.owner) &&
                memberMap.equals(that.memberMap) &&
                homeMap.equals(that.homeMap) &&
                statistics.equals(that.statistics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, displayName, owner, memberMap, homeMap, statistics);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tag", tag)
                .add("displayName", displayName)
                .add("owner", owner)
                .add("memberMap", memberMap)
                .add("homeMap", homeMap)
                .add("statistics", statistics)
                .toString();
    }

    public static abstract class Builder<B extends ClanBase.Builder<B, C>, C extends ClanBase> implements ClanBase.Builder<B, C> {
        protected String tag;
        protected ClanMember owner;
        protected Component displayName;
        protected Map<UUID, ClanMember> members = new HashMap<>();
        protected Map<String, ClanHome> homes = new HashMap<>();
        protected Object2IntMap<StatisticType> statistics = new Object2IntOpenHashMap<>();
        @Override
        public @NotNull B tag(@NotNull String tag) {
            this.tag = tag;
            return self();
        }

        @Override
        public @NotNull B owner(ClanMember owner) {
            this.owner = owner;
            return addMember(owner);
        }

        @Override
        public @NotNull B displayName(@NotNull Component displayName) {
            this.displayName = displayName;
            return self();
        }

        @Override
        public @NotNull B addMember(@NotNull ClanMember member) {
            this.members.put(member.getUniqueId(), member);
            return self();
        }

        @Override
        public @NotNull B removeMember(@NotNull ClanMember member) {
            this.members.remove(member.getUniqueId(), member);
            return self();
        }

        @Override
        public @NotNull B addHome(@NotNull ClanHome home) {
            this.homes.put(home.getName(), home);
            return self();
        }

        @Override
        public @NotNull B removeHome(@NotNull ClanHome home) {
            this.homes.remove(home.getName(), home);
            return self();
        }

        @Override
        public B homes(@NotNull Collection<ClanHome> homes) {
            this.homes.clear();
            this.homes.putAll(FancyCollections.asMap(ClanHome::getName, homes));
            return self();
        }

        @Override
        public @NotNull B members(@NotNull Collection<ClanMember> members) {
            this.members.clear();
            this.members.putAll(FancyCollections.asMap(ClanMember::getUniqueId, members));
            return self();
        }

        @Override
        public @NotNull B statistic(@NotNull StatisticType type, int value) {
            this.statistics.put(type, value);
            return self();
        }

        @Override
        public @NotNull B statistics(@NotNull Map<StatisticType, Integer> statistics) {
            this.statistics.clear();
            this.statistics.putAll(statistics);
            return self();
        }

        @Override
        public @NotNull B emptyStatistics() {
            this.statistics.clear();
            return self();
        }

        @Override
        public @NotNull B emptyMembers() {
            this.members.clear();
            return self();
        }

        @Override
        public @NotNull B emptyHomes() {
            this.homes.clear();
            return self();
        }

        @Override
        public int hashCode() {
            return Objects.hash(tag, owner, displayName, members, homes, statistics);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder<?, ?> builder = (Builder<?, ?>) o;
            return Objects.equals(tag, builder.tag) &&
                    Objects.equals(owner, builder.owner) &&
                    Objects.equals(displayName, builder.displayName) &&
                    members.equals(builder.members) &&
                    homes.equals(builder.homes) &&
                    statistics.equals(builder.statistics);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("tag", tag)
                    .add("owner", owner)
                    .add("displayName", displayName)
                    .add("members", members)
                    .add("homes", homes)
                    .add("statistics", statistics)
                    .toString();
        }
    }
}
