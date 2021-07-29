package org.gepron1x.clans.clan;

import com.google.common.base.Preconditions;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.ClanMemberList;
import org.gepron1x.clans.events.ClanAddMemberEvent;
import org.gepron1x.clans.events.ClanRemoveMemberEvent;
import org.gepron1x.clans.events.ClanSetDisplayNameEvent;
import org.gepron1x.clans.statistic.IntStatisticContainer;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.util.Events;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class Clan {
    private final String tag;
    private Component displayName;
    private final UUID creator;
    private final Map<UUID, ClanMember> members = new HashMap<>();
    private IntStatisticContainer statistics;
    private final ClanMemberList memberList;

    public Clan(@NotNull String tag, @NotNull UUID creator, @NotNull Component displayName) {
        this(tag, creator, displayName, new IntStatisticContainer(), new ClanMemberList());

    }

    public Clan(@NotNull String tag,
                @NotNull UUID creator,
                @NotNull Component displayName,
                @NotNull IntStatisticContainer statistics,
                @NotNull ClanMemberList memberList) {
        this.tag = tag;
        this.creator = creator;
        this.displayName = displayName;
        this.statistics = statistics;
        this.memberList = memberList;
    }

    public void addMember(@NotNull ClanMember member) {
        Preconditions.checkArgument(!isMember(member), "player is already in a clan");

        if(Events.callCancellableEvent(new ClanAddMemberEvent(this, member)))
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
        Preconditions.checkArgument(isMember(uniqueId), String.format("no member with %s uuid", uniqueId));
       removeMember(members.get(uniqueId));
    }
    public void removeMember(@NotNull ClanMember member) {
        Preconditions.checkArgument(members.containsKey(member.getUniqueId()),
                String.format("user %s is not in a clan", member));
        if(Events.callCancellableEvent(new ClanRemoveMemberEvent(this, member)))
        removeMember(member.getUniqueId());
    }
    public void removeMember(@NotNull OfflinePlayer player) {
        removeMember(player.getUniqueId());
    }
    @NotNull
    public Collection<ClanMember> getMembers() {
        return members.values();
    }
    @NotNull
    public String getTag() {
        return tag;
    }

    @NotNull
    public Component getDisplayName() {
        return displayName;
    }

    @NotNull
    public IntStatisticContainer getStatistics() {
        return statistics;
    }

    public void setStatistics(@NotNull IntStatisticContainer statistics) {
        this.statistics = statistics;
    }

    public void setDisplayName(@NotNull Component displayName) {
        if(Events.callCancellableEvent(new ClanSetDisplayNameEvent(this, displayName)))
            this.displayName = displayName;
    }

    public ClanMemberList getMemberList() {
        return memberList;
    }

    @NotNull
    public UUID getCreator() {
        return creator;
    }
}
