package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.wg.WgExtension;

public record RegionCreation(Configs configs, ClanRegion region) {

	public ProtectedRegion create() {
		Location location = region.location();
		double s = this.configs.config().homes().homeRegionRadius();
		double lvl = region.level() + 1;
		double halfSize = Math.pow(1 + this.configs.config().homes().levelRegionScale(), lvl) * s;
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		BlockVector3 first = BlockVector3.at(x - halfSize, y - halfSize, z - halfSize);
		BlockVector3 second = BlockVector3.at(x + halfSize, y + halfSize, z + halfSize);
		ProtectedRegion protectedRegion = new ProtectedCuboidRegion(WgExtension.regionName(region), first, second);
		configs.config().homes().worldGuardFlags().apply(protectedRegion);
		if(!region.shield().expired()) {
			configs.config().shields().shieldFlags().apply(protectedRegion);
			protectedRegion.setFlag(WgExtension.SHIELD_ACTIVE, true);
		}
		protectedRegion.setFlag(WgExtension.REGION_ID, region.id());
		System.out.println(protectedRegion.getFlags());
		return protectedRegion;
	}
}
