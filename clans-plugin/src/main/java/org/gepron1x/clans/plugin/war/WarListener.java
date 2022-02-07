package org.gepron1x.clans.plugin.war;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class WarListener implements Listener {

    private final ClanWarManager warManager;

    public WarListener(@NotNull ClanWarManager warManager) {

        this.warManager = warManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        death(event.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        death(event.getPlayer());
    }

    private void death(Player player) {
        ClanWar war = warManager.getClanWar(player.getUniqueId());
        if(war == null) return;
        war.onDeath(player);

    }
}
