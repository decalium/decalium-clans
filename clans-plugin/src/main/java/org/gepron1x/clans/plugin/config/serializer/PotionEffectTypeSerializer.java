package org.gepron1x.clans.plugin.config.serializer;

import org.bukkit.potion.PotionEffectType;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public final class PotionEffectTypeSerializer implements ValueSerialiser<PotionEffectType> {

	@Override
	public Class<PotionEffectType> getTargetClass() {
		return PotionEffectType.class;
	}

	@Override
	public PotionEffectType deserialise(FlexibleType flexibleType) throws BadValueException {
		return PotionEffectType.getByName(flexibleType.getString());
	}

	@Override
	public Object serialise(PotionEffectType value, Decomposer decomposer) {
		return value.getName();
	}
}
