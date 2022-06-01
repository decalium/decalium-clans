package org.gepron1x.clans.plugin.listener;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gepron1x.clans.api.ClanRepository;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.plugin.cache.ClanCacheImpl;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CacheListener implements Listener {
    private final ClanCacheImpl cache;
    private final Server server;
    private final ClanRepository repository;

    public CacheListener(@NotNull ClanCacheImpl cache, @NotNull Server server, @NotNull ClanRepository repository) {

        this.cache = cache;
        this.server = server;
        this.repository = repository;
    }
    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return;
        this.repository.requestUserClan(uuid).join().ifPresent(cache::cacheClan);

    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Clan clan = cache.getUserClan(player.getUniqueId());
        if(clan == null) return;
        if(areMembersOnline(clan)) return;
        cache.removeClan(clan.tag());

    }




    private boolean areMembersOnline(Clan clan) {
        for(UUID uuid : clan.memberMap().keySet()) {
            if(server.getPlayer(uuid) != null) return true;
        }
        return false;
    }

}
