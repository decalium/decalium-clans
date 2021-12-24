package org.gepron1x.clans.plugin.editor;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.api.editor.HomeEditor;
import org.gepron1x.clans.api.editor.MemberEditor;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.clan.ClanBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class ClanEditorImpl implements ClanEditor {


    private final ClanBuilder builder;
    private final Clan clan;

    public ClanEditorImpl(@NotNull Clan clan, @NotNull ClanBuilder builder) {
        this.builder = builder;
        this.clan = clan;
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
        this.builder.addMember(member);
        return this;
    }

    @Override
    public ClanEditor removeMember(@NotNull ClanMember member) {
        this.builder.removeMember(member);
        return this;
    }

    @Override
    public ClanEditor editMember(@NotNull ClanMember member, @NotNull Consumer<MemberEditor> consumer) {
        ClanMember.Builder memberBuilder = member.toBuilder();
        consumer.accept(new MemberEditorImpl(member, memberBuilder));
        builder.removeMember(member).addMember(memberBuilder.build());
        return this;
    }

    @Override
    public ClanEditor addHome(@NotNull ClanHome home) {
        this.builder.addHome(home);
        return this;
    }

    @Override
    public ClanEditor removeHome(@NotNull ClanHome home) {
        this.builder.removeHome(home);
        return this;
    }

    @Override
    public ClanEditor editHome(@NotNull ClanHome home, @NotNull Consumer<HomeEditor> consumer) {
        ClanHome.Builder homeBuilder = home.toBuilder();
        consumer.accept(new HomeEditorImpl(home, homeBuilder));
        builder.removeHome(home).addHome(homeBuilder.build());
        return this;
    }

    @Override
    public Clan getTarget() {
        return clan;
    }
}
