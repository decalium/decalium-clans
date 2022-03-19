package org.gepron1x.clans.plugin.edition;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.HomeEdition;
import org.gepron1x.clans.api.edition.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class ClanEditionImpl implements ClanEdition {


    private final ClanBuilder builder;
    private final Clan clan;

    public ClanEditionImpl(@NotNull Clan clan, @NotNull ClanBuilder builder) {
        this.builder = builder;
        this.clan = clan;
    }

    @Override
    public ClanEdition setDisplayName(@NotNull Component displayName) {
        this.builder.displayName(displayName);
        return this;
    }

    @Override
    public ClanEdition setStatistic(@NotNull StatisticType type, int value) {
        this.builder.statistic(type, value);
        return this;
    }

    @Override
    public ClanEdition incrementStatistic(@NotNull StatisticType type) {
        this.builder.incrementStatistic(type);
        return this;
    }

    @Override
    public ClanEdition removeStatistic(@NotNull StatisticType type) {
        this.builder.removeStatistic(type);
        return this;
    }

    @Override
    public ClanEdition addMember(@NotNull ClanMember member) {
        Preconditions.checkArgument(this.builder.member(member.uniqueId()) == null, "member with same uuid already in the clan");
        this.builder.addMember(member);
        return this;
    }

    @Override
    public ClanEdition removeMember(@NotNull ClanMember member) {
        Preconditions.checkArgument(this.builder.member(member.uniqueId()) != null, "cannot delete member that is not a clan");
        this.builder.removeMember(member);
        return this;
    }

    @Override
    public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
        ClanMember member = Objects.requireNonNull(this.builder.member(uuid), "no member with given uuid present");
        Preconditions.checkArgument(!this.clan.getOwner().getUniqueId().equals(uuid), "cannot edit clan owner");
        ClanMember.Builder memberBuilder = member.toBuilder();
        consumer.accept(new MemberEditionImpl(member, memberBuilder));
        builder.removeMember(member).addMember(memberBuilder.build());
        return this;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        Preconditions.checkArgument(this.builder.home(home.name()) == null, "Home with same name already in the clan");
        this.builder.addHome(home);
        return this;
    }

    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
        Preconditions.checkArgument(this.builder.home(home.name()) != null, "cannot delete home that is not in the clan");
        this.builder.removeHome(home);
        return this;
    }

    @Override
    public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        ClanHome home = Objects.requireNonNull(this.builder.home(name), "no home with given name present");
        ClanHome.Builder homeBuilder = home.toBuilder();
        consumer.accept(new HomeEditionImpl(home, homeBuilder));
        builder.removeHome(home).addHome(homeBuilder.build());
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanEditionImpl that = (ClanEditionImpl) o;
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
