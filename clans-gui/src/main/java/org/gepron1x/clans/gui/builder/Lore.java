package org.gepron1x.clans.gui.builder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;

public final class Lore {

	private Lore() {}


	public static LoreApplicable description(String... strings) {
		return description(List.of(strings));
	}

	public static LoreApplicable description(List<String> strings) {
		return new DescriptionLoreApplicable(LoreApplicable.text(strings));
	}

	public static LoreApplicable descriptionComponents(List<Component> components) {
		return new DescriptionLoreApplicable(LoreApplicable.components(components));
	}

	public static LoreApplicable interaction(TextColor color, List<String> strings) {
		return new InteractionLoreApplicable(LoreApplicable.text(strings), color);
	}

	public static LoreApplicable interaction(TextColor color, String... strings) {
		return interaction(color, List.of(strings));
	}


}
