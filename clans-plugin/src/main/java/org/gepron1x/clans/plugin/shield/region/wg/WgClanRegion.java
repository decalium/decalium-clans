package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.wg.ProtectedRegionOf;
import org.gepron1x.clans.plugin.wg.WgExtension;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public final class WgClanRegion implements ClanRegion {

	private final ClanRegion region;
	private final RegionContainer container;
	private final Configs configs;

	public WgClanRegion(ClanRegion region, RegionContainer container, Configs configs) {

		this.region = region;
		this.container = container;
		this.configs = configs;
	}

	@Override
	public int id() {
		return region.id();
	}

	@Override
	public ClanReference clan() {
		return region.clan();
	}

	@Override
	public Location location() {
		return region.location();
	}

	@Override
	public Shield shield() {
		return region.shield();
	}


	@Override
	public Shield addShield(Duration duration) {
		region().ifPresent(region -> {
			region.setFlag(WgExtension.SHIELD_ACTIVE, true);
			configs.config().shields().shieldFlags().apply(region);
		});
		return region.addShield(duration);
	}

	@Override
	public void removeShield() {
		region().ifPresent(region -> {
			configs.config().shields().shieldFlags().clear(region);
			configs.config().homes().worldGuardFlags().apply(region);
		});
		region.removeShield();
	}

	public Optional<RegionManager> regionManager() {
		return Optional.ofNullable(container.get(BukkitAdapter.adapt(region.location().getWorld())));
	}

	public Optional<ProtectedRegion> region() {
		return new ProtectedRegionOf(container, region).region();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WgClanRegion that = (WgClanRegion) o;
		return Objects.equals(region, that.region);
	}

	@Override
	public int hashCode() {
		return Objects.hash(region);
	}
}
