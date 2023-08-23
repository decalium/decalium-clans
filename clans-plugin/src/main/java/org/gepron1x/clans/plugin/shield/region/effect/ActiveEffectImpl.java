package org.gepron1x.clans.plugin.shield.region.effect;

import org.gepron1x.clans.api.region.effect.ActiveEffect;
import org.gepron1x.clans.api.region.effect.RegionEffect;

import java.time.Instant;

public record ActiveEffectImpl(RegionEffect effect, Instant end) implements ActiveEffect {
}
