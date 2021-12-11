package org.gepron1x.clans.plugin.clan;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.util.FancyCollections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public record ClanImpl(String tag, UUID owner,
                       Component displayName,
                       Map<UUID, ClanMember> members,
                       Map<String, ClanHome> homes,
                       Object2IntMap<StatisticType> statistics) implements Clan {

    @Override
    public @NotNull String getTag() {
        return tag;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return displayName;
    }

    @Override
    public @NotNull UUID getOwner() {
        return owner;
    }

    @Override
    public @NotNull @Unmodifiable Collection<ClanMember> getMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    @Override
    public @Nullable ClanMember getMember(@NotNull UUID uuid) {
        return members.get(uuid);
    }

    @Override
    public @NotNull @Unmodifiable Collection<ClanHome> getHomes() {
        return Collections.unmodifiableCollection(homes.values());
    }

    @Override
    public OptionalInt getStatistic(@NotNull StatisticType type) {
        int value = statistics.getInt(type);
        return value == statistics.defaultReturnValue() ? OptionalInt.empty() : OptionalInt.of(value);
    }

    @Override
    public @NotNull @Unmodifiable Map<StatisticType, Integer> getStatistics() {
        return Collections.unmodifiableMap(statistics);
    }

    public static ClanImpl.BuilderImpl builder() {
        return new BuilderImpl();
    }

    @Override
    public @NotNull Clan.Builder toBuilder() {
        return builder().tag(tag)
                .owner(owner)
                .displayName(displayName)
                .members(members.values())
                .homes(homes.values())
                .statistics(statistics);
    }

    public static class BuilderImpl implements Clan.Builder {
        private String tag;
        private UUID owner;
        private Component displayName;
        private final Set<ClanMember> members = new HashSet<>();
        private final Set<ClanHome> homes = new HashSet<>();
        private final Object2IntMap<StatisticType> statistics = new Object2IntArrayMap<>();

        @Override
        public @NotNull Builder tag(@NotNull String tag) {
            this.tag = tag;
            return this;
        }

        @Override
        public @NotNull Builder owner(@NotNull UUID owner) {
            this.owner = owner;
            return this;
        }

        @Override
        public @NotNull Builder displayName(@NotNull Component displayName) {
            this.displayName = displayName;
            return this;
        }

        @Override
        public @NotNull Builder addMember(@NotNull ClanMember member) {
            this.members.add(member);
            return this;
        }

        @Override
        public @NotNull Builder removeMember(@NotNull ClanMember member) {
            this.members.remove(member);
            return this;
        }

        @Override
        public @NotNull Builder addHome(@NotNull ClanHome home) {
            this.homes.add(home);
            return this;
        }

        @Override
        public @NotNull Builder removeHome(@NotNull ClanHome home) {
            this.homes.remove(home);
            return this;
        }

        @Override
        public @NotNull Builder homes(@NotNull Collection<ClanHome> homes) {
            this.homes.clear();
            this.homes.addAll(homes);
            return this;
        }

        @Override
        public @NotNull Builder members(@NotNull Collection<ClanMember> members) {
            this.members.clear();
            this.members.addAll(members);
            return this;
        }

        @Override
        public @NotNull Builder statistic(@NotNull StatisticType type, int value) {
            this.statistics.put(type, value);
            return this;
        }

        @Override
        public @NotNull Builder statistics(@NotNull Map<StatisticType, Integer> statistics) {
            this.statistics.clear();
            this.statistics.putAll(statistics);
            return this;
        }

        @Override
        public @NotNull Builder emptyStatistics() {
            this.statistics.clear();
            return this;
        }

        @Override
        public @NotNull Builder emptyMembers() {
            this.members.clear();
            return this;
        }

        @Override
        public @NotNull Builder emptyHomes() {
            this.homes.clear();
            return this;
        }

        @Override
        public @NotNull ClanImpl build() {
            return new ClanImpl(
                    tag,
                    owner,
                    displayName,
                    FancyCollections.asMap(ClanMember::getUniqueId, members),
                    FancyCollections.asMap(ClanHome::getName, homes),
                    new Object2IntArrayMap<>(statistics)
            );
        }
    }
}
