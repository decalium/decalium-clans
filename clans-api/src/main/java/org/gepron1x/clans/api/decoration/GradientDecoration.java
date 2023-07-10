package org.gepron1x.clans.api.decoration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public record GradientDecoration(TextColor first, TextColor second) implements Decoration {


	@Override
	public Component apply(Component component) {
		return MiniMessage.miniMessage().deserialize("<gradient:<first>:<second>><component></gradient>",
				Placeholder.parsed("first", first.asHexString()),
				Placeholder.parsed("second", second.asHexString()),
				Placeholder.component("component", component));
	}
}
