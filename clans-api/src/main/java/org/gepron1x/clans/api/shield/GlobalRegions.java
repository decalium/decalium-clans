package org.gepron1x.clans.api.shield;

import org.gepron1x.clans.api.clan.Clan;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Set;

public interface GlobalRegions {

	Set<ClanRegions> listRegions();

	ClanRegions clanRegions(Clan clan);

	CentralisedFuture<?> remove(int id);


}
