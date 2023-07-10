package org.gepron1x.clans.gui;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class DecaliumColorResolver implements TagResolver {

	private final Map<String, TextColor> colors = new HashMap<>();

	public DecaliumColorResolver() {
		colors.put("positive", TextColor.color(0x42C4FB));
		colors.put("neutral", TextColor.color(0x7CD8D8));
		colors.put("negative", TextColor.color(0xfb2727));
	}

	@Override
	public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
		TextColor color = colors.get(name);
		if (color != null) return Tag.styling(color);
		return null;
	}

	@Override
	public boolean has(@NotNull String name) {
		return colors.containsKey(name);
	}
}
