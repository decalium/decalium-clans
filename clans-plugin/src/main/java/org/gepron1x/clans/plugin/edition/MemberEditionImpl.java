package org.gepron1x.clans.plugin.edition;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.edition.MemberEdition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MemberEditionImpl implements MemberEdition {

    private final ClanMember member;
    private final ClanMember.Builder builder;

    public MemberEditionImpl(@NotNull ClanMember member, @NotNull ClanMember.Builder builder) {

        this.member = member;
        this.builder = builder;
    }

    @Override
    public MemberEdition setRole(@NotNull ClanRole role) {
        builder.role(role);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberEditionImpl that = (MemberEditionImpl) o;
        return member.equals(that.member) && builder.equals(that.builder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, builder);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("member", member)
                .add("builder", builder)
                .toString();
    }
}
