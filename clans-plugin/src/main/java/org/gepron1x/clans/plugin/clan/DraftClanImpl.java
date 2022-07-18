/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.clan;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.util.Optionals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;

public final class DraftClanImpl implements DraftClan {


    private final String tag;
    private final Component displayName;
    private final ClanMember owner;
    private final Map<UUID, ClanMember> memberMap;
    private final Map<String, ClanHome> homeMap;
    private final Map<StatisticType, Integer> statistics;

    DraftClanImpl(String tag,
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
    public @NotNull DraftClan.Builder toBuilder() {
        return new BuilderImpl().tag(this.tag)
                .owner(this.owner)
                .displayName(this.displayName)
                .members(members())
                .homes(homes())
                .statistics(this.statistics);
    }

    @Override
    public @NotNull String tag() {
        return this.tag;
    }

    @Override
    public @NotNull Component displayName() {
        return this.displayName;
    }

    @Override
    public @NotNull ClanMember owner() {
        return this.owner;
    }

    @Override
    public @NotNull @Unmodifiable Collection<? extends ClanMember> members() {
        return this.memberMap.values();
    }

    @Override
    public @NotNull @Unmodifiable Map<UUID, ? extends ClanMember> memberMap() {
        return this.memberMap;
    }

    @Override
    public Optional<ClanMember> member(@NotNull UUID uuid) {
        return Optional.ofNullable(this.memberMap.get(uuid));
    }

    @Override
    public @NotNull @Unmodifiable Collection<? extends ClanHome> homes() {
        return this.homeMap.values();
    }

    @Override
    public Optional<ClanHome> home(@NotNull String name) {
        return Optional.ofNullable(this.homeMap.get(name));
    }

    @Override
    public @NotNull @Unmodifiable Map<String, ? extends ClanHome> homeMap() {
        return this.homeMap;
    }

    @Override
    public OptionalInt statistic(@NotNull StatisticType type) {
        return Optionals.ofNullable(this.statistics.get(type));
    }

    @Override
    public @NotNull @Unmodifiable Map<StatisticType, Integer> statistics() {
        return statistics;
    }

    public static DraftClan.Builder builder() {
        return new BuilderImpl();
    }
    

    public static class BuilderImpl implements DraftClan.Builder {

        private String tag;
        private ClanMember owner;
        private Component displayName;
        private final Map<UUID, ClanMember> members = new HashMap<>();
        private final Map<String, ClanHome> homes = new HashMap<>();
        private final Map<StatisticType, Integer> statistics = new HashMap<>();



        @Override
        public @NotNull Builder tag(@NotNull String tag) {
            this.tag = tag;
            return this;
        }

        @Override
        public @NotNull Builder owner(ClanMember owner) {
            this.owner = owner;
            return this.addMember(owner);
        }

        @Override
        public @NotNull Builder displayName(@NotNull Component displayName) {
            this.displayName = displayName;
            return this;
        }

        @Override
        public @NotNull Builder addMember(@NotNull ClanMember member) {
            this.members.put(member.uniqueId(), member);
            return this;
        }

        @Override
        public @NotNull Builder removeMember(@NotNull ClanMember member) {
            this.members.remove(member.uniqueId(), member);
            return this;
        }

        @Override
        public @NotNull Builder addHome(@NotNull ClanHome home) {
            this.homes.put(home.name(), home);
            return this;
        }

        @Override
        public @NotNull Builder removeHome(@NotNull ClanHome home) {
            this.homes.remove(home.name(), home);
            return this;
        }

        @Override
        public @NotNull Builder homes(@NotNull Collection<? extends ClanHome> homes) {
            this.homes.clear();
            homes.forEach(this::addHome);
            return this;
        }

        @Override
        public @NotNull Builder members(@NotNull Collection<? extends ClanMember> members) {
            this.members.clear();
            members.forEach(this::addMember);
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
        public Builder self() {
            return this;
        }

        @Override
        public @NotNull DraftClan build() {
            return new DraftClanImpl(
                    Objects.requireNonNull(tag),
                    Objects.requireNonNull(displayName),
                    Objects.requireNonNull(owner),
                    Map.copyOf(members),
                    Map.copyOf(homes),
                    Map.copyOf(statistics)
            );
        }

        @Override
        public void applyEdition(Consumer<ClanEdition> consumer) {
            consumer.accept(new ClanEditionImpl());

        }

        private final class ClanEditionImpl implements ClanEdition {

            @Override
            public ClanEdition rename(@NotNull Component displayName) {
                BuilderImpl.this.displayName(displayName);
                return this;
            }

            @Override
            public ClanEdition setStatistic(@NotNull StatisticType type, int value) {
                BuilderImpl.this.statistic(type, value);
                return this;
            }

            @Override
            public ClanEdition owner(@NotNull ClanMember owner) {
                Preconditions.checkArgument(BuilderImpl.this.members.containsValue(owner));
                BuilderImpl.this.owner = owner;
                return this;
            }

            @Override
            public ClanEdition addStatistics(@NotNull Map<StatisticType, Integer> statistics) {
                statistics.forEach((key, value) -> BuilderImpl.this.statistics.merge(key, value, Integer::sum));
                return this;
            }

            @Override
            public ClanEdition incrementStatistic(@NotNull StatisticType type) {
                BuilderImpl.this.statistics.merge(type, 1, Integer::sum);
                return this;
            }

            @Override
            public ClanEdition removeStatistic(@NotNull StatisticType type) {
                BuilderImpl.this.statistics.remove(type);
                return this;
            }

            @Override
            public ClanEdition addMember(@NotNull ClanMember member) {
                BuilderImpl.this.addMember(member);
                return this;
            }

            @Override
            public ClanEdition removeMember(@NotNull ClanMember member) {
                BuilderImpl.this.removeMember(member);
                return this;
            }

            @Override
            public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
                ClanMember member = Objects.requireNonNull(BuilderImpl.this.members.get(uuid));
                ClanMember.Builder builder = member.toBuilder();
                builder.applyEdition(consumer);
                BuilderImpl.this.removeMember(member).addMember(builder.build());
                return this;
            }

            @Override
            public ClanEdition addHome(@NotNull ClanHome home) {
                BuilderImpl.this.addHome(home);
                return this;
            }

            @Override
            public ClanEdition removeHome(@NotNull ClanHome home) {
                BuilderImpl.this.removeHome(home);
                return this;
            }

            @Override
            public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
                ClanHome home = Objects.requireNonNull(BuilderImpl.this.homes.get(name));
                ClanHome.Builder builder = home.toBuilder();
                builder.applyEdition(consumer);
                BuilderImpl.this.removeHome(home).addHome(builder.build());
                return this;
            }
        }
    }



}
