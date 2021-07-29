package org.gepron1x.clans.clan;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.Buildable;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.Objects.*;

public class ClanBuilder implements Buildable.Builder<Clan> {
    private String tag;
    private ClanMember creator;
    private Component displayName;
    private final Object2IntMap<StatisticType> statistics = new Object2IntArrayMap<>();
    private final Set<ClanMember> members = new HashSet<>();

    public ClanBuilder(String tag) {
        this.tag = tag;
    }

    public ClanBuilder tag(@NotNull String tag) {
        this.tag = tag;
        return this;
    }

    public ClanBuilder creator(@NotNull ClanMember creator) {
        this.creator = creator;
        return this;
    }


    public ClanBuilder members(Iterable<? extends ClanMember> members) {
        this.members.clear();
        members.forEach(this.members::add);
        return this;
    }

    public ClanBuilder members(Collection<? extends ClanMember> members) {
        this.members.clear();
        this.members.addAll(members);
        return this;
    }
    public ClanBuilder emptyMembers() {
        this.members.clear();
        return this;
    }
    public ClanBuilder addMember(ClanMember member) {
        members.add(member);
        return this;
    }
    public ClanBuilder members(ClanMember... members) {
        return members(Arrays.asList(members));
    }
    public ClanBuilder displayName(Component displayName) {
        this.displayName = displayName;
        return this;
    }
    public ClanBuilder statistics(Map<StatisticType, @NotNull Integer> stats) {
        statistics.clear();
        statistics.putAll(stats);
        return this;
    }
    public ClanBuilder setStatistic(StatisticType type, int value) {
        statistics.put(type, value);
        return this;
    }
    public ClanBuilder emptyStatistics() {
        statistics.clear();
        return this;
    }
    public ClanMember creator() { return creator; }
    public Set<ClanMember> members() { return Collections.unmodifiableSet(members); }
    public Object2IntMap<StatisticType> statistics() { return statistics; }
    public Component displayName() { return displayName; }
    public String tag() { return tag; }
    @Override
    public @NotNull Clan build() {
        return new Clan(
                requireNonNull(tag, "tag"),
                requireNonNull(displayName, "displayName"),
                requireNonNull(creator, "creator"),
                members,
                statistics
        );
    }
}
