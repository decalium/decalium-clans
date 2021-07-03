package com.manya.clans.manager;

import com.manya.clans.DecaliumClans;
import com.manya.clans.clan.Clan;
import com.manya.clans.clan.member.ClanMember;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.*;

public class ClanManager {
    private final Map<String, Clan> clansByTag = new HashMap<>();
    private final Map<UUID, Clan> userClans = new HashMap<>();

    private final DecaliumClans plugin;

    public ClanManager(DecaliumClans plugin) {
        this.plugin = plugin;

    }



    public void addClan(@NotNull Clan clan, boolean loadMembers) {

        checkArgument(!clansByTag.containsKey(clan.getTag()), "clan with this tag already exists.");
        clansByTag.put(clan.getTag(), clan);
        userClans.put(clan.getCreator(), clan);
        if(loadMembers)
            clan.getMemberList()
                    .getMembers()
                    .forEach(member -> userClans.put(member.getUniqueId(), clan));

    }
    public void addClan(@NotNull Clan clan) {
        addClan(clan, false);
    }
    public void removeClan(@NotNull Clan clan) {
        checkArgument(clansByTag.remove(clan.getTag(), clan), "this clan wasn't registered.");
        clan.getMemberList().getMembers().forEach(member -> userClans.remove(member.getUniqueId()));
    }
    public void setPlayerClan(UUID uuid, @Nullable Clan clan) {
        checkArgument(clan == null || !clan.getMemberList().isMember(uuid),
                "this player is already member of this clan."
        );
        userClans.put(uuid, clan);
    }
    public void setPlayerClan(OfflinePlayer player, @Nullable Clan clan) {
        setPlayerClan(player.getUniqueId(), clan);
    }


    @Unmodifiable
    @NotNull
    public Collection<Clan> getClans() {
        return clansByTag.values();
    }
    @Nullable
    public Clan getClan(String tag) {
        return clansByTag.get(tag);
    }
    @Nullable
    public Clan getUserClan(UUID playerUuid) {
        Clan clan = userClans.get(playerUuid);
        if(clan == null) return null;
        if(clan.getMemberList().isMember(playerUuid)) return clan;
        userClans.remove(playerUuid);
        return null;
    }
    @Nullable
    public Clan getUserClan(OfflinePlayer player) {
        return getUserClan(player.getUniqueId());
    }

}

