package org.gepron1x.clans.api.region.effect;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public interface RegionEffect {

	String name();

	ItemStack icon();


	void onEnter(Player player, Duration duration);

	void onLeave(Player player);

}
