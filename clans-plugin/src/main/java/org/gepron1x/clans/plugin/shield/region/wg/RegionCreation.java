package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.wg.WgExtension;

public record RegionCreation(Configs configs, ClanRegion region) {

	public ProtectedRegion create() {

		ProtectedRegion protectedRegion = create(
				WgExtension.regionName(region),
				region.location(),
				configs.config().homes().homeRegionRadius()
		);

		configs.config().homes().worldGuardFlags().apply(protectedRegion);
		if (region.shield().active()) {
			configs.config().shields().shieldFlags().apply(protectedRegion);
			protectedRegion.setFlag(WgExtension.SHIELD_ACTIVE, true);
		}
		protectedRegion.setFlag(WgExtension.REGION_ID, region.id());
		return protectedRegion;
	}

	public static ProtectedRegion create(String name, Location location, double radius) {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		BlockVector3 first = BlockVector3.at(x - radius, y - radius, z - radius);
		BlockVector3 second = BlockVector3.at(x + radius, y + radius, z + radius);
		return new ProtectedCuboidRegion(name, true, first, second);
	}
}
