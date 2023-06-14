package org.gepron1x.clans.gui.builder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.List;

public final class InteractionLoreApplicable implements LoreApplicable {
	private final LoreApplicable applicable;
	private final TextColor color;

	public InteractionLoreApplicable(LoreApplicable applicable, TextColor color) {
		this.applicable = applicable;
		this.color = color;
	}
	@Override
	public List<Component> lore(TagResolver resolver) {
		List<Component> lines = applicable.lore(resolver);
		if(lines.size() == 0) return List.of();
		List<Component> lore = new ArrayList<>(lines.size());
		lore.add(Component.text().content("⇄ ").append(lines.get(0)).color(color).build());
		for(int i = 1; i < lines.size(); i++) lore.add(lines.get(i).color(color));
		return lore;
	}


}
