package org.gepron1x.clans.events.member;

import org.bukkit.event.Event;
import org.gepron1x.clans.clan.member.ClanMember;

public abstract class MemberEvent extends Event {
    private final ClanMember member;

    public MemberEvent(ClanMember member) {
        this.member = member;
    }

    public ClanMember getMember() {
        return member;
    }
}
