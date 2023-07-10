package org.gepron1x.clans.api.chat;

import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public final class BooleanStateResolver implements TagResolver {
	private final String name;
	private final boolean is;

	public BooleanStateResolver(String name, boolean is) {
		this.name = name;
		this.is = is;
	}

	@Override
	public @NotNull Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
		Tag trueValue = Tag.preProcessParsed(arguments.popOr("True value not present").value());
		Tag falseValue = Tag.preProcessParsed(arguments.popOr("False value not present").value());
		return is ? trueValue : falseValue;
	}

	@Override
	public boolean has(@NotNull String name) {
		return this.name.equals(name);
	}
}
