package org.gepron1x.clans.plugin;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class ClanCache {
    private final Map<String, Clan> clanMap = new ConcurrentHashMap<>();
    private final Map<UUID, Clan> userClanCache = new ConcurrentHashMap<>();

    public ClanCache() {

    }


    public Clan getUserClan(UUID uuid) {
        return userClanCache.get(uuid);
    }

    public Clan getClan(String tag) {
        return clanMap.get(tag);
    }

    public void cacheClan(Clan clan) {
        clanMap.put(clan.getTag(), clan);
        userClanCache.entrySet().removeIf(entry -> entry.getValue().getTag().equals(clan.getTag()));

        for(ClanMember member : clan.getMembers()) {
            userClanCache.put(member.getUniqueId(), clan);
        }
    }
    public void removeClan(Clan clan) {
        clanMap.remove(clan.getTag(), clan);
        userClanCache.entrySet().removeIf(entry -> entry.getValue().equals(clan));
    }







}
