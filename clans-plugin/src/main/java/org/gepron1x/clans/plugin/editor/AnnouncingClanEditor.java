package org.gepron1x.clans.plugin.editor;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.api.editor.HomeEditor;
import org.gepron1x.clans.api.editor.MemberEditor;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class AnnouncingClanEditor implements ClanEditor {
    private final Clan clan;
    private final Server server;
    private final ClanEditor delegate;
    private final MessagesConfig messages;

    public AnnouncingClanEditor(Clan clan, Server server, ClanEditor delegate, MessagesConfig messages) {
        this.clan = clan;
        this.server = server;
        this.delegate = delegate;
        this.messages = messages;
    }
    @Override
    public ClanEditor setDisplayName(@NotNull Component displayName) {
        delegate.setDisplayName(displayName);
        clan.sendMessage(messages.announcements().clanSetDisplayName().with("name", displayName));
        return this;
    }

    @Override
    public ClanEditor setStatistic(@NotNull StatisticType type, int value) {
        delegate.setStatistic(type, value);
        return this;
    }

    @Override
    public ClanEditor incrementStatistic(@NotNull StatisticType type) {
        delegate.incrementStatistic(type);
        return this;
    }

    @Override
    public ClanEditor removeStatistic(@NotNull StatisticType type) {
        delegate.removeStatistic(type);
        return this;
    }

    @Override
    public ClanEditor addMember(@NotNull ClanMember member) {
        delegate.addMember(member);
        clan.sendMessage(messages.announcements().memberAdded().with("member", member.renderName(server)));
        return this;
    }

    @Override
    public ClanEditor removeMember(@NotNull ClanMember member) {
        delegate.removeMember(member);
        clan.sendMessage(messages.announcements().memberRemoved().with("member", member.renderName(server)));
        return this;
    }

    @Override
    public ClanEditor editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEditor> consumer) {
        delegate.editMember(uuid, memberEditor -> consumer.accept(new AnnouncingMemberEditor(requireNonNull(clan.getMember(uuid)), memberEditor)));
        return this;
    }

    @Override
    public ClanEditor addHome(@NotNull ClanHome home) {
        delegate.addHome(home);
        clan.sendMessage(messages.announcements().homeCreated()
                .with("member", requireNonNull(clan.getMember(home.getCreator())).renderName(server))
                .with("home_name", home));
        return this;
    }

    @Override
    public ClanEditor removeHome(@NotNull ClanHome home) {
        delegate.removeHome(home);
        clan.sendMessage(messages.announcements().homeDeleted()
                .with("home_name", home));
        return this;
    }

    @Override
    public ClanEditor editHome(@NotNull String name, @NotNull Consumer<HomeEditor> consumer) {
        delegate.editHome(name, consumer);
        return this;
    }

    private class AnnouncingMemberEditor implements MemberEditor {

        private final ClanMember member;
        private final MemberEditor delegate;

        AnnouncingMemberEditor(ClanMember member, MemberEditor delegate) {


            this.member = member;
            this.delegate = delegate;
        }

        @Override
        public MemberEditor setRole(@NotNull ClanRole role) {
            delegate.setRole(role);
            clan.sendMessage(messages.announcements().memberPromoted().with("member", member.renderName(server)));
            return this;
        }
    }
}
