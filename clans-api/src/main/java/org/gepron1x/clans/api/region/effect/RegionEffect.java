package org.gepron1x.clans.api.region.effect;

import org.bukkit.entity.Player;

import java.time.Duration;

public interface RegionEffect {

	String name();



	void onEnter(Player player, Duration duration);

	void onLeave(Player player);

}
