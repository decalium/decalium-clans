package org.gepron1x.clans.plugin.economy;

import org.bukkit.Location;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.ClanRegions;
import org.gepron1x.clans.plugin.config.settings.PricesConfig;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EconomyRegions implements ClanRegions {

	private final ClanRegions regions;
	private final VaultPlayer player;
	private final PricesConfig prices;

	public EconomyRegions(ClanRegions regions, VaultPlayer player, PricesConfig prices) {

		this.regions = regions;
		this.player = player;
		this.prices = prices;

	}

	@Override
	public Collection<ClanRegion> regions() {
		return regions.regions().stream().map(r -> new EconomyRegion(r, player, prices)).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public Optional<ClanRegion> region(int id) {
		return regions.region(id).map(r -> new EconomyRegion(r, player, prices));
	}

	@Override
	public Optional<ClanRegion> region(Location location) {
		return regions.region(location).map(r -> new EconomyRegion(r, player, prices));
	}

	@Override
	public void remove(ClanRegion region) {
		regions.remove(region);
	}

	@Override
	public ClanRegion create(Location location) {
		return new EconomyRegion(regions.create(location), player, prices);
	}

	@Override
	public void clear() {
		regions.clear();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EconomyRegions that = (EconomyRegions) o;
		return Objects.equals(regions, that.regions) && Objects.equals(player, that.player) && Objects.equals(prices, that.prices);
	}

	@Override
	public int hashCode() {
		return Objects.hash(regions, player, prices);
	}
}
