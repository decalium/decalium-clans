package org.gepron1x.clans.plugin.edition;

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
    private final ClanEdition delegate;
    private final MessagesConfig messages;

    public AnnouncingClanEdition(Clan clan, Server server, ClanEdition delegate, MessagesConfig messages) {
        this.clan = clan;
        this.server = server;
        this.delegate = delegate;
        this.messages = messages;
    }
    @Override
    public ClanEdition setDisplayName(@NotNull Component displayName) {
        delegate.setDisplayName(displayName);
        clan.sendMessage(messages.announcements().clanSetDisplayName().with("name", displayName));
        return this;
    }

    @Override
    public ClanEdition setStatistic(@NotNull StatisticType type, int value) {
        delegate.setStatistic(type, value);
        return this;
    }

    @Override
    public ClanEdition incrementStatistic(@NotNull StatisticType type) {
        delegate.incrementStatistic(type);
        return this;
    }

    @Override
    public ClanEdition removeStatistic(@NotNull StatisticType type) {
        delegate.removeStatistic(type);
        return this;
    }

    @Override
    public ClanEdition addMember(@NotNull ClanMember member) {
        delegate.addMember(member);
        clan.sendMessage(messages.announcements().memberAdded().with("member", member.renderName(server)));
        return this;
    }

    @Override
    public ClanEdition removeMember(@NotNull ClanMember member) {
        delegate.removeMember(member);
        clan.sendMessage(messages.announcements().memberRemoved().with("member", member.renderName(server)));
        return this;
    }

    @Override
    public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
        delegate.editMember(uuid, memberEditor -> consumer.accept(new AnnouncingMemberEdition(requireNonNull(clan.getMember(uuid)), memberEditor)));
        return this;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        delegate.addHome(home);
        clan.sendMessage(messages.announcements().homeCreated()
                .with("member", requireNonNull(clan.getMember(home.getCreator())).renderName(server))
                .with("home_name", home));
        return this;
    }

    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
        delegate.removeHome(home);
        clan.sendMessage(messages.announcements().homeDeleted()
                .with("home_name", home));
        return this;
    }

    @Override
    public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        delegate.editHome(name, consumer);
        return this;
    }

    private class AnnouncingMemberEdition implements MemberEdition {

        private final ClanMember member;
        private final MemberEdition delegate;

        AnnouncingMemberEdition(ClanMember member, MemberEdition delegate) {


            this.member = member;
            this.delegate = delegate;
        }

        @Override
        public MemberEdition setRole(@NotNull ClanRole role) {
            delegate.setRole(role);
            clan.sendMessage(messages.announcements().memberPromoted().with("member", member.renderName(server)));
            return this;
        }
    }
}
