package org.gepron1x.clans.plugin.war;

import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;

import java.util.Collection;
import java.util.Optional;

public interface Wars {


    War create(ClanReference first, ClanReference second);

    Optional<War> currentWar(Player player);

    Collection<War> currentWars();

    void onDeath(Player player);


}
