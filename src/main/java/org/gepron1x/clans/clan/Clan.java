package org.gepron1x.clans.clan;

import com.google.common.base.Preconditions;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.ClanMemberList;
import org.gepron1x.clans.statistic.IntStatisticContainer;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class Clan {
    private final String tag;
    private Component displayName;
    private final UUID creator;
    private final Map<UUID, ClanMember> members = new HashMap<>();
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

    public void addMember(@NotNull ClanMember member) {
        Preconditions.checkArgument(!members.containsValue(member), "player is already in a clan");
        members.put(member.getUniqueId(), member);
    }
    public boolean isMember(@NotNull ClanMember member) {
        return members.containsValue(member);
    }
    public boolean isMember(@NotNull UUID uniqueId) {
        return members.containsKey(uniqueId);
    }
    public boolean isMember(@NotNull OfflinePlayer player) {
        return isMember(player.getUniqueId());
    }

    public void removeMember(@NotNull UUID uniqueId) {
        Preconditions.checkArgument(members.containsKey(uniqueId), String.format("user %s is not in a clan", uniqueId));
        members.remove(uniqueId);
    }
    public void removeMember(@NotNull ClanMember member) {
        removeMember(member.getUniqueId());
    }
    public void removeMember(@NotNull OfflinePlayer player) {
        removeMember(player.getUniqueId());
    }
    @NotNull
    public Collection<ClanMember> getMembers() {
        return members.values();
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
