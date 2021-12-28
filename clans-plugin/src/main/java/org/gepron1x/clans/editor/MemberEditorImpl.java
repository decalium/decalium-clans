package org.gepron1x.clans.editor;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.editor.MemberEditor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MemberEditorImpl implements MemberEditor {

    private final ClanMember member;
    private final ClanMember.Builder builder;

    public MemberEditorImpl(@NotNull ClanMember member, @NotNull ClanMember.Builder builder) {

        this.member = member;
        this.builder = builder;
    }

    @Override
    public ClanMember getTarget() {
        return member;
    }

    @Override
    public MemberEditor setRole(@NotNull ClanRole role) {
        builder.role(role);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberEditorImpl that = (MemberEditorImpl) o;
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
