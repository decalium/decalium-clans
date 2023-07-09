package org.gepron1x.clans.api.decoration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public record ColorDecoration(TextColor color) implements Decoration {

	@Override
	public Component apply(Component component) {
		return component.color(color);
	}
}
