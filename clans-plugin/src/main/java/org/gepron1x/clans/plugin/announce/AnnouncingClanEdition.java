package org.gepron1x.clans.plugin.announce;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.HomeEdition;
import org.gepron1x.clans.api.edition.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class AnnouncingClanEdition implements ClanEdition {
    private final Clan clan;
    private final Server server;
    private final MessagesConfig messages;

    public AnnouncingClanEdition(Clan clan, Server server, MessagesConfig messages) {
        this.clan = clan;
        this.server = server;
        this.messages = messages;
    }
    @Override
    public ClanEdition rename(@NotNull Component displayName) {
        clan.sendMessage(messages.announcements().clanSetDisplayName().with("name", displayName));
        return this;
    }

    @Override
    public ClanEdition setStatistic(@NotNull StatisticType type, int value) {
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
        clan.sendMessage(messages.announcements().memberAdded().with("member", member.renderName(server)));
        return this;
    }

    @Override
    public ClanEdition removeMember(@NotNull ClanMember member) {
        clan.sendMessage(messages.announcements().memberRemoved().with("member", member.renderName(server)));
        return this;
    }

    @Override
    public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
        consumer.accept(new AnnouncingMemberEdition(clan.member(uuid).orElseThrow()));
        return this;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        clan.sendMessage(messages.announcements().homeCreated()
                .with("member", requireNonNull(clan.member(home.creator())).orElseThrow().renderName(server))
                .with("home_name", home));
        return this;
    }

    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
        clan.sendMessage(messages.announcements().homeDeleted()
                .with("home_name", home));
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
        public MemberEdition setRole(@NotNull ClanRole role) {
            clan.sendMessage(messages.announcements().memberPromoted().with("member", member.renderName(server)));
            return this;
        }
    }
}
