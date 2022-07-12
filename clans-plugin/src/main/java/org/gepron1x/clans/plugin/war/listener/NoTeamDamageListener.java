package org.gepron1x.clans.plugin.war.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.gepron1x.clans.plugin.util.pdc.OwnedEntity;
import org.gepron1x.clans.plugin.war.Wars;

import java.util.Optional;

public final class NoTeamDamageListener implements Listener {

    private final Wars wars;

    public NoTeamDamageListener(Wars wars) {
        this.wars = wars;
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCrystalExplode(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if(entity.getType() != EntityType.ENDER_CRYSTAL) return;
        if(!(event.getDamager() instanceof Player damager)) return;
        new OwnedEntity(entity).owner(damager.getUniqueId());

    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Optional<Player> opt = getDamager(event.getDamager());
        if(opt.isEmpty()) return;
        Player damager = opt.orElseThrow();
        if(!(event.getEntity() instanceof Player player)) return;
        wars.currentWar(damager).flatMap(war -> war.team(damager)).ifPresent(team -> {
            if(team.isMember(player)) {
                event.setCancelled(true);
            }
        });
    }

    public Optional<Player> getDamager(Entity entity) {
        if(entity instanceof Player player) return Optional.of(player);
        return new OwnedEntity(entity).owner().map(entity.getServer()::getPlayer);
    }


}
