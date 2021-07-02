package com.manya.clans.clan;

import com.manya.clans.clan.member.ClanMemberList;
import com.manya.clans.statistic.IntStatisticContainer;
import net.kyori.adventure.text.Component;

import java.util.UUID;


public class Clan {
    private final String tag;
    private Component displayName;
    private final UUID creator;
    private IntStatisticContainer statistics;
    private final ClanMemberList memberList;

    public Clan(String tag, UUID creator, Component displayName) {
        this(tag, creator, displayName, new IntStatisticContainer(), new ClanMemberList());

    }

    public Clan(String tag, UUID creator, Component displayName, IntStatisticContainer statistics, ClanMemberList memberList) {
        this.tag = tag;
        this.creator = creator;
        this.displayName = displayName;
        this.statistics = statistics;
        this.memberList = memberList;
    }

    public String getTag() {
        return tag;
    }


    public Component getDisplayName() {
        return displayName;
    }

    public IntStatisticContainer getStatistics() {
        return statistics;
    }

    public void setStatistics(IntStatisticContainer statistics) {
        this.statistics = statistics;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public ClanMemberList getMemberList() {
        return memberList;
    }


    public UUID getCreator() {
        return creator;
    }
}
