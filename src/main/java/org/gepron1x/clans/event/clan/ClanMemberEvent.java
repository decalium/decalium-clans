package org.gepron1x.clans.event.clan;

import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;

public abstract class ClanMemberEvent extends ClanEvent {
    private final ClanMember member;
    public ClanMemberEvent(@NotNull Clan clan, @NotNull ClanMember member) {
        super(clan);
        this.member = member;
    }

    public ClanMember getMember() {
        return member;
    }
}
