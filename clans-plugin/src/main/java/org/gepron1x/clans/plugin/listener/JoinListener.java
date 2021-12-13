package org.gepron1x.clans.plugin.listener;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.plugin.ClanCacheImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class JoinListener implements Listener {
    private final ClanCacheImpl cache;
    private final ClanStorage storage;

    public JoinListener(@NotNull ClanCacheImpl cache, @NotNull ClanStorage storage) {

        this.cache = cache;
        this.storage = storage;
    }

    public void onJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return; // clan is already loaded!

        Clan loadedClan = storage.loadUserClan(uuid); // that blocks the async thread, but, thats necessary.
        if(loadedClan != null) {
            cache.cacheClan(loadedClan);
        }

    }

    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Clan clan = cache.getUserClan(player.getUniqueId());
        Server server = player.getServer();

        if(clan == null) return;

        for(UUID uuid : clan.memberMap().keySet()) { // check if there's any other online members of the clan
            if(server.getPlayer(uuid) != null) {
                return;
            }
        }
        cache.removeClan(clan);

    }
}
