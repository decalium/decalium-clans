package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.gepron1x.clans.api.shield.ClanRegion;

import java.util.Optional;

public record ProtectedRegionOf(RegionContainer container, ClanRegion clanRegion) {


	public Optional<ProtectedRegion> region() {
		return Optional.ofNullable(container.get(BukkitAdapter.adapt(clanRegion.location().getWorld())))
				.map(manager -> manager.getRegion("clans_region_"+clanRegion.id()));
	}
}
