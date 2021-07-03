package com.manya.clans.clan.member;

import com.google.common.base.Preconditions;
import com.manya.clans.clan.role.ClanRole;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClanMemberList implements Iterable<ClanMember> {

    private final Map<UUID, ClanMember> members = new HashMap<>();

    public ClanMemberList() {
    }

    public void addMember(ClanMember member) {
        Preconditions.checkArgument(!members.containsKey(member.getUniqueId()), "player is already in clan");
        members.put(member.getUniqueId(), member);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }
    public void removeMember(OfflinePlayer player) {removeMember(player.getUniqueId());}
    public void removeMember(ClanMember member) {
        members.remove(member.getUniqueId(), member);
    }

    public Collection<ClanMember> getMembers() {
        return members.values();
    }

    public boolean isMember(UUID uuid) {
        return members.containsKey(uuid);
    }
    public boolean isMember(ClanMember member) {return isMember(member.getUniqueId()); }
    public boolean isMember(OfflinePlayer player) {return isMember(player.getUniqueId()); }

    @Nullable
    public ClanMember getMember(UUID uuid) {
        return members.get(uuid);
    }
    @Nullable public ClanMember getMember(OfflinePlayer player) {return getMember(player.getUniqueId()); }


    @NotNull
    @Override
    public Iterator<ClanMember> iterator() {
        return members.values().iterator();
    }
}
