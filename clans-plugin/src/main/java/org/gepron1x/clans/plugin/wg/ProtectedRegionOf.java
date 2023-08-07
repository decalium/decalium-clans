package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.gepron1x.clans.api.region.ClanRegion;

import java.util.Optional;

public record ProtectedRegionOf(RegionContainer container, ClanRegion clanRegion) {


	public Optional<ProtectedRegion> region() {
		return regionManager().map(manager -> manager.getRegion(WgExtension.regionName(clanRegion)));
	}

	public void remove() {
		regionManager().ifPresent(manager -> manager.removeRegion(WgExtension.regionName(clanRegion)));
	}

	public Optional<RegionManager> regionManager() {
		return Optional.ofNullable(container.get(BukkitAdapter.adapt(clanRegion.location().getWorld())));
	}
}
