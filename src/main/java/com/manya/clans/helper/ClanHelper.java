package com.manya.clans.helper;

import com.google.common.base.Preconditions;
import com.manya.clans.DecaliumClans;
import com.manya.clans.clan.Clan;
import com.manya.clans.clan.member.ClanMember;
import com.manya.clans.storage.ClanDao;
import com.manya.clans.storage.ClanMemberDao;
import com.manya.clans.storage.StatisticDao;
import com.manya.clans.util.TaskScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ClanHelper {

    private final TaskScheduler scheduler;
    private final Jdbi jdbi;
    private final Map<String, Clan> clansByTag = new HashMap<>();
    private final Map<UUID, Clan> userClans = new HashMap<>();


    public ClanHelper(TaskScheduler scheduler, Jdbi jdbi) {

        this.scheduler = scheduler;
        this.jdbi = jdbi;

    }



    public void addClan(Clan clan) {
        clansByTag.put(clan.getTag(), clan);
        userClans.put(clan.getCreator(), clan);
        clan.getMemberList().forEach(member -> userClans.put(member.getUniqueId(), clan));
    }
    public void createClan(Clan clan) {
        addClan(clan);
        scheduler.async(task -> jdbi.useExtension(ClanDao.class, dao -> dao.insertClan(clan)));
    }
    public void removeClan(Clan clan) {
        clansByTag.remove(clan.getTag());
        clan.getMemberList().forEach(member -> userClans.remove(member.getUniqueId()));


        scheduler.async(task -> {
            jdbi.useExtension(ClanDao.class, dao -> dao.removeClan(clan));
            jdbi.useExtension(ClanMemberDao.class, dao -> dao.clearMembers(clan));
        });
    }
    public void setClanDisplayName(Clan clan, Component displayName) {
        clan.setDisplayName(displayName);
        scheduler.async(task -> jdbi.useExtension(ClanDao.class, dao -> dao.setDisplayName(clan, displayName)));
    }
    public void removeMember(Clan clan, ClanMember member) {
        Preconditions.checkArgument(clan.getMemberList().isMember(member));
        clan.getMemberList().removeMember(member);
        userClans.remove(member.getUniqueId());
        scheduler.async(task -> jdbi.useExtension(ClanMemberDao.class, dao -> dao.removeMember(member)));

    }
    public void addMember(Clan clan, ClanMember member) {
        Preconditions.checkArgument(!userClans.containsKey(member.getUniqueId()), "player is already in a clan");
        clan.getMemberList().addMember(member);
        userClans.put(member.getUniqueId(), clan);
        scheduler.async(task -> jdbi.useExtension(ClanMemberDao.class, dao -> dao.addMember(member, clan)));
    }
    @Nullable
    public Clan getUserClan(OfflinePlayer player) {
        return getUserClan(player.getUniqueId());
    }
    public Collection<Clan> getClans() {
        return clansByTag.values();
    }
    @Nullable
    public Clan getUserClan(UUID uuid) {
        return userClans.get(uuid);
    }
    @Nullable
    public Clan getClan(String tag) {
        return clansByTag.get(tag);
    }


}
