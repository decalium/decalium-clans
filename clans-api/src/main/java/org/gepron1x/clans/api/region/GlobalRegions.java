package org.gepron1x.clans.api.region;

import org.bukkit.block.Block;
import org.gepron1x.clans.api.clan.Clan;

import java.util.Optional;
import java.util.Set;

public interface GlobalRegions {

	Set<ClanRegions> listRegions();

	ClanRegions clanRegions(Clan clan);

	Optional<ClanRegion> region(int id);
	void remove(int id);

	Optional<Integer> regionId(Block block);


}
