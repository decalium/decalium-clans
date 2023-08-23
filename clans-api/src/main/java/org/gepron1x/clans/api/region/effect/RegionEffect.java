package org.gepron1x.clans.api.region.effect;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface RegionEffect {

	String name();

	Component displayName();


	void onEnter(Player player);

	void onLeave(Player player);

}
