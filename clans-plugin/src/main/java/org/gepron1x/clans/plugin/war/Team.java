package org.gepron1x.clans.plugin.war;

import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.plugin.util.player.PlayerReference;

import java.util.Collection;

public interface Team {

    ClanReference clan();

    Collection<PlayerReference> members();

    Collection<PlayerReference> alive();

    boolean onDeath(Player player);

    boolean isAlive();

}
