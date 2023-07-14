package org.gepron1x.clans.plugin.util;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import org.bukkit.DyeColor;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class DyeColors { // sorry adventure

	private DyeColors() {}



	public static @NotNull DyeColor nearestTo(final @NotNull TextColor any) {
		requireNonNull(any, "color");
		float matchedDistance = Float.MAX_VALUE;
		var values = DyeColor.values();
		DyeColor match = values[0];
		for (int i = 0, length = values.length; i < length; i++) {
			final DyeColor potential = values[i];
			var color = potential.getColor();
			final float distance = distance(any.asHSV(), HSVLike.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
			if (distance < matchedDistance) {
				match = potential;
				matchedDistance = distance;
			}
			if (distance == 0) {
				break; // same colour! whoo!
			}
		}


		return match;
	}

	private static float distance(final @NotNull HSVLike self, final @NotNull HSVLike other) {
		// weight hue more heavily than saturation and brightness. kind of magic numbers, but is fine for our use case of downsampling to a set of colors
		final float hueDistance = 3 * Math.min(Math.abs(self.h() - other.h()), 1f - Math.abs(self.h() - other.h()));
		final float saturationDiff = self.s() - other.s();
		final float valueDiff = self.v() - other.v();
		return hueDistance * hueDistance + saturationDiff * saturationDiff + valueDiff * valueDiff;
	}
}
