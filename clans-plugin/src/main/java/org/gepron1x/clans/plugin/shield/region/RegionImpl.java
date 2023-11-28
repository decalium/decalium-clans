package org.gepron1x.clans.plugin.shield.region;

import com.google.common.base.MoreObjects;
import org.bukkit.Location;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.Shield;
import org.gepron1x.clans.api.region.effect.ActiveEffect;
import org.gepron1x.clans.api.region.effect.RegionEffect;
import org.gepron1x.clans.plugin.shield.ShieldImpl;
import org.gepron1x.clans.plugin.shield.region.effect.ActiveEffectImpl;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class RegionImpl implements ClanRegion {

	private final int id;
	private final ClanReference clan;
	private final Location location;
	private Shield shield;
	@Nullable
	private ActiveEffect effect;

	public RegionImpl(int id, ClanReference clan, Location location, Shield shield, @Nullable ActiveEffect effect) {

		this.id = id;
		this.clan = clan;
		this.location = location;
		this.shield = shield;
		this.effect = effect;
	}

	public RegionImpl(int id, ClanReference clan, Location location, Shield shield) {
		this(id, clan, location, shield, null);
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public ClanReference clan() {
		return clan;
	}

	@Override
	public Location location() {
		return this.location.clone();
	}

	@Override
	public Shield shield() {
		return this.shield;
	}


	@Override
	public Shield addShield(Duration duration) {
		Instant now = Instant.now();
		Instant end = now.plus(duration);
		this.shield = new ShieldImpl(now, end);
		return this.shield;
	}

	@Override
	public ActiveEffect applyEffect(RegionEffect effect, Duration duration) {
		return this.effect = new ActiveEffectImpl(effect, Instant.now().plus(duration));
	}

	@Override
	public Optional<ActiveEffect> activeEffect() {
		if(this.effect == null) return Optional.empty();
		if(!this.effect.active()) {
			this.effect = null;
			return Optional.empty();
		}
		return Optional.of(effect);
	}

	@Override
	public void removeShield() {
		this.shield = Shield.NONE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RegionImpl region = (RegionImpl) o;
		return id == region.id && Objects.equals(clan, region.clan) && Objects.equals(location, region.location) && Objects.equals(shield, region.shield);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, clan, location, shield);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("clan", clan)
				.add("location", location)
				.add("shield", shield)
				.toString();
	}
}
