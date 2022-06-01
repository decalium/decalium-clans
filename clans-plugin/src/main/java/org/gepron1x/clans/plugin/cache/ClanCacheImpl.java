package org.gepron1x.clans.plugin.cache;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ClanCacheImpl {
    private final Map<String, Clan> clanMap = new ConcurrentHashMap<>();
    private final Map<UUID, Clan> userClanMap = new ConcurrentHashMap<>();

    public ClanCacheImpl() {

    }

    @Nullable
    public Clan getUserClan(@NotNull UUID uuid) {
        return userClanMap.get(uuid);

    }

    public @NotNull @UnmodifiableView Collection<DraftClan> getClans() {
        return Collections.unmodifiableCollection(clanMap.values());
    }



    public boolean isCached(@NotNull String tag) {
        return clanMap.containsKey(tag);
    }

    @Nullable
    public Clan getClan(@NotNull String tag) {
        return clanMap.get(tag);
    }


    public void cacheClan(Clan clan) {
        clanMap.put(clan.tag(), clan);
        for(ClanMember member : clan.members()) {
            userClanMap.put(member.uniqueId(), clan);
        }
    }


    public void removeClan(String tag) {
        Clan clan = clanMap.remove(tag);
        if(clan == null) return;
        for(UUID uuid : clan.memberMap().keySet()) userClanMap.remove(uuid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("cachedClans", clanMap.values())
                .toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanCacheImpl clanCache = (ClanCacheImpl) o;
        return clanMap.equals(clanCache.clanMap) && userClanMap.equals(clanCache.userClanMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clanMap, userClanMap);
    }
}
