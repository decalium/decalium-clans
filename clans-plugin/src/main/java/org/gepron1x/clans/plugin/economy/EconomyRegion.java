package org.gepron1x.clans.plugin.economy;

import com.google.common.base.MoreObjects;
import org.bukkit.Location;
import org.gepron1x.clans.api.exception.NotEnoughMoneyException;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.plugin.config.settings.PricesConfig;

import java.time.Duration;
import java.util.Objects;

public final class EconomyRegion implements ClanRegion {

	private final ClanRegion region;
	private final VaultPlayer player;
	private final PricesConfig prices;


	public EconomyRegion(ClanRegion region, VaultPlayer player, PricesConfig prices) {
		this.region = region;
		this.player = player;
		this.prices = prices;
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
		if (!player.has(prices.shield())) {
			throw new NotEnoughMoneyException(prices.notEnoughMoney().with("price", prices.shield()), prices.shield(), player.balance());
		}
		return region.addShield(duration);
	}

	@Override
	public void removeShield() {
		region.removeShield();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EconomyRegion that = (EconomyRegion) o;
		return Objects.equals(region, that.region) && Objects.equals(player, that.player);
	}

	@Override
	public int hashCode() {
		return Objects.hash(region, player);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("region", region)
				.add("player", player)
				.toString();
	}
}
