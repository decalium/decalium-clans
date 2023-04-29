package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.wg.ProtectedRegionOf;
import org.gepron1x.clans.plugin.wg.WgExtension;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.time.Duration;
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
	public int level() {
		return region.level();
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
	public CentralisedFuture<ClanRegion> upgrade() {
		return region.upgrade().thenApplySync(r -> {
			regionManager().ifPresent(manager -> {
				String name = WgExtension.regionName(r);
				ProtectedRegion pr = manager.getRegion(name);
				DefaultDomain domain = new DefaultDomain();
				if(pr != null) {
					domain.addAll(pr.getMembers());
					manager.removeRegion(name);
				}
				ProtectedRegion newRegion = new RegionCreation(configs, r).create();
				newRegion.setMembers(domain);
				manager.addRegion(newRegion);
			});
			return new WgClanRegion(region, container, configs);
		});
	}

	@Override
	public CentralisedFuture<ClanRegion> addShield(Duration duration) {

		return region.addShield(duration).thenApplySync(r -> {
			region().ifPresent(region -> {
				region.setFlag(WgExtension.SHIELD_ACTIVE, true);
				configs.config().shields().shieldFlags().apply(region);
			});
			return new WgClanRegion(r, container, configs);
		});
	}

	@Override
	public CentralisedFuture<ClanRegion> removeShield() {
		return region.removeShield().thenApplySync(r -> {
			region().ifPresent(region -> {
				configs.config().shields().shieldFlags().clear(region);
				configs.config().homes().worldGuardFlags().apply(region);
			});
			return new WgClanRegion(r, container, configs);
		});
	}

	public Optional<RegionManager> regionManager() {
		return Optional.ofNullable(container.get(BukkitAdapter.adapt(region.location().getWorld())));
	}

	public Optional<ProtectedRegion> region() {
		return new ProtectedRegionOf(container, region).region();
	}
}
