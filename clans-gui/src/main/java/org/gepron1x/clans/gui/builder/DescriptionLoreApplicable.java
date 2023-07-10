package org.gepron1x.clans.gui.builder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.List;

public final class DescriptionLoreApplicable implements LoreApplicable {

	private static final Component DESCRIPTION_SEPARATOR = Component.text("├─ ", NamedTextColor.GRAY);
	private static final Component DESCRIPTION_SPACE = Component.text("│ ", NamedTextColor.GRAY);
	private static final Component DESCRIPTION_END = Component.text("└─ ", NamedTextColor.GRAY);


	private final LoreApplicable applicable;

	public DescriptionLoreApplicable(LoreApplicable applicable) {

		this.applicable = applicable;
	}

	@Override
	public List<Component> lore(TagResolver resolver) {
		List<Component> lines = applicable.lore(resolver);
		if (lines.size() == 0) return List.of();
		List<Component> lore = new ArrayList<>(lines.size());
		for (int i = 0; i < lines.size() - 1; i++) {
			Component value = lines.get(i);
			if (value.equals(Component.space())) lore.add(DESCRIPTION_SPACE);
			else
				lore.add(Component.text().append(DESCRIPTION_SEPARATOR).append(value.colorIfAbsent(NamedTextColor.WHITE)).build());
		}
		lore.add(Component.text().append(DESCRIPTION_END).append(lines.get(lines.size() - 1)).colorIfAbsent(NamedTextColor.WHITE).build());
		return lore;
	}
}
