package org.gepron1x.clans.plugin.war.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.gepron1x.clans.plugin.war.Wars;

public final class NoTeamDamageListener implements Listener {

    private final Wars wars;

    public NoTeamDamageListener(Wars wars) {
        this.wars = wars;
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player player)) return;
        wars.currentWar(damager).flatMap(war -> war.team(damager)).ifPresent(team -> {
            if(team.isMember(player)) {
                event.setCancelled(true);
            }
        });
    }


}
