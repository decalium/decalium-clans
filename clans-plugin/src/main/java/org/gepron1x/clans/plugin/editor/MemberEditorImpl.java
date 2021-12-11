package org.gepron1x.clans.plugin.editor;

import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.editor.MemberEditor;
import org.jetbrains.annotations.NotNull;

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
}
