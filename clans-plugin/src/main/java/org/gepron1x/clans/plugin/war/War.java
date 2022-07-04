package org.gepron1x.clans.plugin.war;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface War {

    Collection<Team> teams();


    boolean onPlayerDeath(Player player);

    boolean isEnded();


}
