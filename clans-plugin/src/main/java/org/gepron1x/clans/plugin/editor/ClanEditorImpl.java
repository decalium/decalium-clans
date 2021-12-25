package org.gepron1x.clans.plugin.editor;

import com.google.common.base.MoreObjects;
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

import java.util.Objects;
import java.util.UUID;
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
        this.builder.incrementStatistic(type);
        return this;
    }

    @Override
    public ClanEditor removeStatistic(@NotNull StatisticType type) {
        this.builder.removeStatistic(type);
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
    public ClanEditor editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEditor> consumer) {
        ClanMember member = Objects.requireNonNull(this.builder.member(uuid), "no member with given uuid present");
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
    public ClanEditor editHome(@NotNull String name, @NotNull Consumer<HomeEditor> consumer) {
        ClanHome home = Objects.requireNonNull(this.builder.home(name), "no home with given name present");
        ClanHome.Builder homeBuilder = home.toBuilder();
        consumer.accept(new HomeEditorImpl(home, homeBuilder));
        builder.removeHome(home).addHome(homeBuilder.build());
        return this;
    }

    @Override
    public Clan getTarget() {
        return clan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanEditorImpl that = (ClanEditorImpl) o;
        return builder.equals(that.builder) && clan.equals(that.clan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(builder, clan);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("builder", builder)
                .add("clan", clan)
                .toString();
    }
}
