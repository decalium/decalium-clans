package org.gepron1x.clans.plugin.shield.region;

import org.bukkit.Location;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.plugin.shield.ShieldImpl;

import java.time.Duration;
import java.time.Instant;

public final class RegionImpl implements ClanRegion {

	private final int id;
	private final ClanReference clan;
	private final Location location;
	private Shield shield;

	public RegionImpl(int id, ClanReference clan, Location location, Shield shield) {

		this.id = id;
		this.clan = clan;
		this.location = location;
		this.shield = shield;
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
	public void removeShield() {
		this.shield = Shield.NONE;
	}
}
