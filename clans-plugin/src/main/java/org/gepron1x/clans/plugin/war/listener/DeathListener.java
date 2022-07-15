package org.gepron1x.clans.plugin.war.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gepron1x.clans.api.war.Wars;

public final class DeathListener implements Listener {

    private final Wars wars;

    public DeathListener(Wars wars) {
        this.wars = wars;
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerDeathEvent event) {
        wars.onDeath(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        wars.onDeath(event.getPlayer());
    }
}
