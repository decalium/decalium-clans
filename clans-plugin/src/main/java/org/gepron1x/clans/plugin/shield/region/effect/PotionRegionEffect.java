package org.gepron1x.clans.plugin.shield.region.effect;

import io.papermc.paper.util.Tick;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.gepron1x.clans.api.region.effect.RegionEffect;

import java.time.Duration;


public record PotionRegionEffect(String name, PotionEffect effect) implements RegionEffect {


	@Override
	public void onEnter(Player player, Duration duration) {
		player.addPotionEffect(effect.withDuration(Tick.tick().fromDuration(duration)));
	}

	@Override
	public void onLeave(Player player) {
		player.removePotionEffect(effect.getType());
	}
}
