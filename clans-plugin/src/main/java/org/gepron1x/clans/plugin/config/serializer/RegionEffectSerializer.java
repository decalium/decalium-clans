package org.gepron1x.clans.plugin.config.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.gepron1x.clans.api.region.effect.RegionEffect;
import org.gepron1x.clans.plugin.shield.region.effect.PotionRegionEffect;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RegionEffectSerializer implements ValueSerialiser<RegionEffect> {
	@Override
	public Class<RegionEffect> getTargetClass() {
		return RegionEffect.class;
	}

	@Override
	public RegionEffect deserialise(FlexibleType flexibleType) throws BadValueException {
		Map<String, FlexibleType> map = flexibleType.getMap((key, value) -> Map.entry(key.getString(), value));
		PotionEffectType type = map.get("type").getObject(PotionEffectType.class);
		int duration = map.get("duration").getInteger();
		int amplifier = map.get("amplifier").getInteger();


		String name = map.get("name").getString();
		Component displayName = map.get("display-name").getObject(Component.class);
		Material material = map.get("icon").getEnum(Material.class);

		ItemStack icon = new ItemStack(material);
		icon.editMeta(meta -> meta.displayName(displayName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)));


		PotionEffect potionEffect = new PotionEffect(type, duration, amplifier);

		return new PotionRegionEffect(name, icon, potionEffect);
	}

	@Override
	public Object serialise(RegionEffect value, Decomposer decomposer) {
		Map<String, Object> map = new LinkedHashMap<>();
		if(!(value instanceof PotionRegionEffect effect)) throw new IllegalStateException("Don't know how to serialize" + value);
		map.put("name", effect.name());
		map.put("display-name", decomposer.decompose(Component.class, effect.icon().getItemMeta().displayName()));
		map.put("icon", decomposer.decompose(Material.class, effect.icon().getType()));
		map.put("type", decomposer.decompose(PotionEffectType.class, effect.effect().getType()));
		map.put("duration", effect.effect().getDuration());
		map.put("amplifier", effect.effect().getAmplifier());
		return map;
	}
}
