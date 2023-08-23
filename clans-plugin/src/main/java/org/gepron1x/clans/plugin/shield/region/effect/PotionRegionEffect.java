package org.gepron1x.clans.plugin.shield.region.effect;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.gepron1x.clans.api.region.effect.RegionEffect;

import java.util.Objects;


public final class PotionRegionEffect implements RegionEffect {

	private final String name;
	private final Component displayName;
	private final PotionEffect effect;

	public PotionRegionEffect(String name, Component displayName, PotionEffect effect) {
		this.name = name;
		this.displayName = displayName;
		this.effect = effect;
	}


	@Override
	public String name() {
		return this.name;
	}

	@Override
	public Component displayName() {
		return this.displayName;
	}

	@Override
	public void onEnter(Player player) {
		player.addPotionEffect(effect);
	}

	@Override
	public void onLeave(Player player) {
		player.removePotionEffect(effect.getType());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PotionRegionEffect that = (PotionRegionEffect) o;
		return Objects.equals(name, that.name) && Objects.equals(displayName, that.displayName) && Objects.equals(effect, that.effect);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, displayName, effect);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("name", name)
				.add("displayName", displayName)
				.add("effect", effect)
				.toString();
	}
}
