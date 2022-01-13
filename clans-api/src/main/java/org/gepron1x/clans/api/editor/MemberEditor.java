package org.gepron1x.clans.api.editor;

import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;

public interface MemberEditor extends Editor<ClanMember> {

    @Override
    @NotNull
    default Class<ClanMember> getTarget() {
        return ClanMember.class;
    }

    MemberEditor setRole(@NotNull ClanRole role);

}
