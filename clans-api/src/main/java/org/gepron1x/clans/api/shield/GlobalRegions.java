package org.gepron1x.clans.api.shield;

import org.gepron1x.clans.api.clan.Clan;

import java.util.Set;

public interface GlobalRegions {

	Set<ClanRegions> listRegions();

	ClanRegions clanRegions(Clan clan);

	void remove(int id);


}
