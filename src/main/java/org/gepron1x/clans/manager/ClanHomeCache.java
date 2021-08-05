package org.gepron1x.clans.manager;

import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.home.ClanHome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ClanHomeCache {
    private final ClanManager clanManager;
    private final Map<String, Clan> owningClans;
    public ClanHomeCache(ClanManager clanManager) {
        this.clanManager = clanManager;
        Collection<Clan> clans = clanManager.getClans();
        owningClans = new HashMap<>(clans.size());
        for(Clan clan : clans) {
            Collection<ClanHome> clanHomes = clan.getHomes();
            for(ClanHome home : clanHomes) owningClans.put(home.getName(), clan);
        }
    }
    @Nullable
    public Clan getOwningClan(String name) {
        Clan clan = owningClans.get(name);
        if(clan != null) {
            if(clan.hasHome(name)) return clan;
            owningClans.remove(name);
        }
        for(Clan c : clanManager.getClans()) {
            if(!c.hasHome(name)) continue;
            owningClans.put(name, c);
            return c;
        }
        return null;

    }

    @Nullable
    public Clan getOwningClan(@NotNull ClanHome home) {
        return getOwningClan(home.getName());
    }





}
