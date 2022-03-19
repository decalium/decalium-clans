package org.gepron1x.clans.plugin.listener;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.event.ClanCreatedEvent;
import org.gepron1x.clans.api.event.ClanDeletedEvent;
import org.gepron1x.clans.api.event.ClanEditedEvent;
import org.gepron1x.clans.plugin.ClanCacheImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CacheListener implements Listener {
    private final ClanCacheImpl cache;
    private final Server server;
    private final ClanStorage storage;

    public CacheListener(@NotNull ClanCacheImpl cache, @NotNull Server server, @NotNull ClanStorage storage) {

        this.cache = cache;
        this.server = server;
        this.storage = storage;
    }
    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return;

        @Nullable IdentifiedDraftClan loadedClan = storage.loadUserClan(uuid);
        if(loadedClan != null) {
            cache.cacheClan(loadedClan);
        }

    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        @Nullable IdentifiedDraftClan clan = cache.getUserClan(player.getUniqueId());
        if(clan == null) return;
        if(areMembersOnline(clan)) return;
        cache.removeClan(clan.id());

    }

    @EventHandler
    public void onClanCreation(ClanCreatedEvent event) {
        @Nullable IdentifiedDraftClan clan = event.getClan();
        if(!areMembersOnline(clan)) return;
        cache.cacheClan(event.getClan());
    }




    private boolean areMembersOnline(@Nullable IdentifiedDraftClan clan) {
        for(UUID uuid : clan.memberMap().keySet()) {
            if(server.getPlayer(uuid) != null) return true;
        }

        return false;
    }

    @EventHandler
    public void onClanDeletion(ClanDeletedEvent event) {
        cache.removeClan(event.getClan());
    }

    @EventHandler
    public void onClanEdit(ClanEditedEvent event) {
        if(!cache.isCached(event.getClan().getTag())) return;
        cache.removeClan(event.getClan());
        cache.cacheClan(event.getResult());

    }
}
