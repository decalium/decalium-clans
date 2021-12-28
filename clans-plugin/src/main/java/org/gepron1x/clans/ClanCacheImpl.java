package org.gepron1x.clans;

import org.gepron1x.clans.api.ClanCache;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClanCacheImpl implements ClanCache {
    private final Map<String, Clan> clanMap = new ConcurrentHashMap<>();
    private final Map<UUID, Clan> userClanMap = new ConcurrentHashMap<>();

    public ClanCacheImpl() {

    }
    @Override
    @Nullable
    public Clan getUserClan(@NotNull UUID uuid) {
        return userClanMap.get(uuid);

    }

    @Override
    public @NotNull @UnmodifiableView Collection<Clan> getClans() {
        return Collections.unmodifiableCollection(clanMap.values());
    }

    @Override
    public boolean isCached(@NotNull Clan clan) {
        return clanMap.containsValue(clan);
    }


    @Override
    public boolean isCached(@NotNull String tag) {
        return clanMap.containsKey(tag);
    }

    @Override
    @Nullable
    public Clan getClan(@NotNull String tag) {
        return clanMap.get(tag);
    }

    public void cacheClan(Clan clan) {
        clanMap.put(clan.getTag(), clan);
        userClanMap.entrySet().removeIf(entry -> entry.getValue().getTag().equals(clan.getTag()));

        for(ClanMember member : clan.getMembers()) {
            userClanMap.put(member.getUniqueId(), clan);
        }
    }
    public void removeClan(Clan clan) {
        clanMap.remove(clan.getTag(), clan);
        clan.memberMap().keySet().forEach(userClanMap::remove);
    }







}
