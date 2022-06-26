package org.gepron1x.clans.plugin.wg;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;

public record NameForRegion(Clan clan, ClanHome home) {


    public String value() {
        return "decaliumclans_"+clan.tag()+"_"+home.name();
    }

}
