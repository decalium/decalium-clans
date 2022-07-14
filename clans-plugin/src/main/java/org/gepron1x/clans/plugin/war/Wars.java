package org.gepron1x.clans.plugin.war;

import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.war.Team;

import java.util.Collection;
import java.util.Optional;

public interface Wars {

    void start(War war);

    War create(Team first, Team second);

    Team createTeam(ClanReference ref);

    Optional<War> currentWar(Player player);

    Collection<War> currentWars();

    void onDeath(Player player);

    void end(War war);

    void cleanEnded();


}
