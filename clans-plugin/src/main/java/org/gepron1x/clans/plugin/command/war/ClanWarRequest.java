package org.gepron1x.clans.plugin.command.war;

import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.api.war.Wars;

public record ClanWarRequest(Wars wars, ClanReference actor, ClanReference victim) {


    public void accept() {
        Team first = wars.createTeam(actor);
        Team second = wars.createTeam(victim);
        wars.start(wars.create(first, second));

    }
}
