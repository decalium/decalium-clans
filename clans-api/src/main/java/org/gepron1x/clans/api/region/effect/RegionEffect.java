package org.gepron1x.clans.api.region.effect;

import org.bukkit.entity.Player;

public interface RegionEffect {

	String name();


	void onEnter(Player player);

	void onLeave(Player player);

}
