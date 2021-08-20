package org.gepron1x.clans.clan.home;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.gepron1x.clans.ClanManager;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.event.clan.ClanDeletedEvent;
import org.gepron1x.clans.event.home.ClanAddHomeEvent;
import org.gepron1x.clans.event.home.ClanRemoveHomeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ClanHomeCache implements Listener {
    private final Map<String, Clan> owningClans;
    public ClanHomeCache(ClanManager clanManager) {
        Collection<Clan> clans = clanManager.getClans();
        owningClans = new HashMap<>(clans.size());
        for(Clan clan : clans) for (ClanHome home : clan.getHomes()) {
            owningClans.put(home.getName(), clan);
        }

    }
    @Nullable
    public Clan getOwningClan(String name) {
        return owningClans.get(name);
    }

    @Nullable
    public Clan getOwningClan(@NotNull ClanHome home) {
        return getOwningClan(home.getName());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClanDeletion(ClanDeletedEvent event) {
        owningClans.entrySet().removeIf(entry -> entry.getValue().equals(event.getClan()));
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(ClanAddHomeEvent event) {
        owningClans.put(event.getHome().getName(), event.getClan());
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(ClanRemoveHomeEvent event) {
        owningClans.remove(event.getHome().getName());
    }




}
