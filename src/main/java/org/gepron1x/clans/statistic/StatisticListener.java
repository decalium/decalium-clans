package org.gepron1x.clans.statistic;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.ClanManager;

import java.util.Objects;

public class StatisticListener implements Listener {
    private final ClanManager manager;

    public StatisticListener(ClanManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        Clan victimClan = manager.getUserClan(victim);
        if(killer != null) {
            Clan killerClan = manager.getUserClan(killer);
            if(Objects.equals(killerClan, victimClan)) return;
            if(killerClan != null) {
                killerClan.incrementStatistic(StatisticType.KILLS);
            }
        }
        if(victimClan != null) {
            victimClan.incrementStatistic(StatisticType.DEATHS);
        }
    }


}
