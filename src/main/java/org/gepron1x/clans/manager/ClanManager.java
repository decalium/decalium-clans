package org.gepron1x.clans.manager;

import com.google.common.base.Preconditions;
import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.events.clan.ClanCreatedEvent;
import org.gepron1x.clans.events.clan.ClanDeletedEvent;
import org.gepron1x.clans.util.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ClanManager {
    private final Map<UUID, Clan> userClans = new HashMap<>();
    private final Map<String, Clan> clans = new HashMap<>();

    public Collection<Clan> getClans() {
        return clans.values();
    }
    public void insertClan(@NotNull Clan clan) {
        Preconditions.checkArgument(!clans.containsValue(clan), "clan already added");
        clans.put(clan.getTag(), clan);
        for(ClanMember member : clan.getMembers()) userClans.put(member.getUniqueId(), clan);
    }
    public void addClan(@NotNull Clan clan) {
        if(Events.callCancellableEvent(new ClanCreatedEvent(clan))) insertClan(clan);
    }
    @Nullable
    public Clan getClan(String tag) {
        return clans.get(tag);
    }
    @Nullable
    public Clan getUserClan(@NotNull UUID uuid) {
        Clan clan = userClans.get(uuid);
        if(clan != null) {
            if(clan.isMember(uuid)) return clan;
            userClans.remove(uuid);
            clan = null;
        }
        for(Clan c : getClans()) {
            if (!c.isMember(uuid)) continue;
            clan = c;
        }
        if(clan != null) userClans.put(uuid, clan);
        return clan;
    }
    @Nullable public Clan getUserClan(@NotNull OfflinePlayer player) {return getUserClan(player.getUniqueId()); }

    public void removeClan(@NotNull Clan clan) {
        Preconditions.checkArgument(clans.containsValue(clan), "clan is not registered");
        if(!Events.callCancellableEvent(new ClanDeletedEvent(clan))) return;
        clans.remove(clan.getTag(), clan);
        userClans.entrySet().removeIf(entry -> entry.getValue().equals(clan));

    }



}
