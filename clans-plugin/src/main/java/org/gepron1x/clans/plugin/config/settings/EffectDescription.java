package org.gepron1x.clans.plugin.config.settings;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.gepron1x.clans.api.region.effect.RegionEffect;

import java.time.Duration;
import java.util.List;

public interface EffectDescription {

	RegionEffect effect();
	Material material();

	Component name();

	List<Component> lore();

	double price();


	Duration duration();


}
