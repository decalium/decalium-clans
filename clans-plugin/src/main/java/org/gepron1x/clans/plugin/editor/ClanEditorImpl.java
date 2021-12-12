package org.gepron1x.clans.plugin.editor;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.api.editor.HomeEditor;
import org.gepron1x.clans.api.editor.MemberEditor;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class ClanEditorImpl implements ClanEditor {

    private final Clan clan;

    private final Clan.Builder builder;

    public ClanEditorImpl(@NotNull Clan clan, @NotNull Clan.Builder builder) {
        this.clan = clan;
        this.builder = builder;
    }

    @Override
    public ClanEditor setDisplayName(@NotNull Component displayName) {
        this.builder.displayName(displayName);
        return this;
    }

    @Override
    public ClanEditor setStatistic(@NotNull StatisticType type, int value) {
        this.builder.statistic(type, value);
        return this;
    }

    @Override
    public ClanEditor incrementStatistic(@NotNull StatisticType type) {

        return this;
    }

    @Override
    public ClanEditor removeStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEditor addMember(@NotNull ClanMember member) {
        builder.addMember(member);
        return this;
    }

    @Override
    public ClanEditor removeMember(@NotNull ClanMember member) {
        builder.removeMember(member);
        return this;
    }

    @Override
    public ClanEditor editMember(@NotNull ClanMember member, @NotNull Consumer<MemberEditor> consumer) {
        ClanMember.Builder memberBuilder = member.toBuilder();
        MemberEditor editor = new MemberEditorImpl(member, memberBuilder);
        consumer.accept(editor);
        builder.removeMember(member).addMember(memberBuilder.build());
        return this;
    }

    @Override
    public ClanEditor addHome(@NotNull ClanHome home) {
        builder.addHome(home);
        return this;
    }

    @Override
    public ClanEditor removeHome(@NotNull ClanHome home) {
        builder.removeHome(home);
        return this;
    }

    @Override
    public ClanEditor editHome(@NotNull ClanHome home, @NotNull Consumer<HomeEditor> consumer) {
        builder.removeHome(home);
        ClanHome.Builder homeBuilder = home.toBuilder();
        HomeEditor editor = new HomeEditorImpl(home, homeBuilder);
        consumer.accept(editor);
        builder.addHome(homeBuilder.build());
        return this;
    }

    @Override
    public Clan getTarget() {
        return clan;
    }
}
