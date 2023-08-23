package org.gepron1x.clans.api.region.effect;

import java.time.Duration;
import java.time.Instant;

public interface ActiveEffect {

	RegionEffect effect();

	Instant end();

	default boolean active() {
		return Instant.now().isAfter(end());
	}

	default Duration left() {
		Duration left = Duration.between(Instant.now(), end());
		if (left.isNegative()) return Duration.ZERO;
		return left;
	}
}
