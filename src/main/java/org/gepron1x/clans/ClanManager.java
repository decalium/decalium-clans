package org.gepron1x.clans;

import com.google.common.base.Preconditions;
import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.event.clan.ClanCreatedEvent;
import org.gepron1x.clans.event.clan.ClanDeletedEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ClanManager {

    private final Map<UUID, Clan> userClansMap;
    private final Map<String, Clan> clansMap;

    public ClanManager(@NotNull Collection<Clan> clans) {
        this.userClansMap = new HashMap<>();
        this.clansMap = new HashMap<>(clans.size());
        for(Clan clan : clans) {
            insertClan(clan);
        }
    }
    public ClanManager() {
        this(Collections.emptySet());
    }

    public Collection<Clan> getClans() {
        return Collections.unmodifiableCollection(clansMap.values());
    }
    @ApiStatus.Internal
    void insertClan(@NotNull Clan clan) {
        Preconditions.checkArgument(!clansMap.containsValue(clan), "clan already added");
        clansMap.put(clan.getTag(), clan);
        for(ClanMember member : clan.getMembers()) userClansMap.put(member.getUniqueId(), clan);
    }
    public void addClan(@NotNull Clan clan) {
        if(new ClanCreatedEvent(clan).callEvent()) insertClan(clan);
    }
    @Nullable
    public Clan getClan(String tag) {
        return clansMap.get(tag);
    }
    @Nullable
    public Clan getUserClan(@NotNull UUID uuid) {

        Clan clan = userClansMap.get(uuid);
        if(clan != null) {
            if(clan.isMember(uuid)) return clan;
            userClansMap.remove(uuid);
            clan = null;
        }
        for(Clan c : getClans()) {
            if (!c.isMember(uuid)) continue;
            clan = c;
            userClansMap.put(uuid, clan);
            break;
        }
        return clan;
    }
    @Nullable public Clan getUserClan(@NotNull OfflinePlayer player) {return getUserClan(player.getUniqueId()); }

    public void removeClan(@NotNull Clan clan) {
        Preconditions.checkArgument(clansMap.containsValue(clan), "clan is not registered");
        if(new ClanDeletedEvent(clan).callEvent()) return;
        clansMap.remove(clan.getTag(), clan);
        userClansMap.entrySet().removeIf(entry -> entry.getValue().equals(clan));
    }



}
