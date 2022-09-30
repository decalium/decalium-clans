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
package org.gepron1x.clans.plugin.announce;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class AnnouncingClanEdition implements ClanEdition {
    private final Clan clan;
    private final Audience audience;
    private final Server server;
    private final MessagesConfig messages;

    public AnnouncingClanEdition(Clan clan, Audience audience, Server server, MessagesConfig messages) {
        this.clan = clan;
        this.audience = audience;
        this.server = server;
        this.messages = messages;
    }
    @Override
    public ClanEdition rename(@NotNull Component displayName) {
        this.audience.sendMessage(messages.announcements().clanSetDisplayName().with("name", displayName));
        return this;
    }

    @Override
    public ClanEdition setStatistic(@NotNull StatisticType type, int value) {
        return this;
    }

    @Override
    public ClanEdition owner(@NotNull ClanMember owner) {
        this.audience.sendMessage(messages.announcements().clanOwnerChanged().with("member", owner.renderName(server)));
        return this;
    }

    @Override
    public ClanEdition addStatistics(@NotNull Map<StatisticType, Integer> statistics) {
        return this;
    }

    @Override
    public ClanEdition incrementStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEdition removeStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEdition addMember(@NotNull ClanMember member) {
        this.audience.sendMessage(messages.announcements().memberAdded().with("member", member.renderName(server)));
        return this;
    }

    @Override
    public ClanEdition removeMember(@NotNull ClanMember member) {
        this.audience.sendMessage(messages.announcements().memberRemoved().with("member", member.renderName(server)));
        return this;
    }

    @Override
    public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
        consumer.accept(new AnnouncingMemberEdition(clan.member(uuid).orElseThrow()));
        return this;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        this.audience.sendMessage(messages.announcements().homeCreated()
                .with("member", requireNonNull(clan.member(home.creator())).orElseThrow().renderName(server))
                .with("home_name", home.displayName()));
        return this;
    }

    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
        this.audience.sendMessage(messages.announcements().homeDeleted()
                .with("member", clan.member(home.creator()).orElseThrow().renderName(server))
                .with("home_name", home.displayName()));
        return this;
    }

    @Override
    public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        return this;
    }

    private class AnnouncingMemberEdition implements MemberEdition {

        private final ClanMember member;

        AnnouncingMemberEdition(ClanMember member) {

            this.member = member;

        }

        @Override
        public MemberEdition appoint(@NotNull ClanRole role) {
            AnnouncingClanEdition.this.audience.sendMessage(messages.announcements().memberPromoted()
                    .with("member", member.renderName(server))
                    .with("role", role)
            );
            return this;
        }
    }
}
