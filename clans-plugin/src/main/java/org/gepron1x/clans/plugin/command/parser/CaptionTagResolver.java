/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.command.parser;

import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.exceptions.parsing.ParserException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CaptionTagResolver implements TagResolver.WithoutArguments {

	private final ParserException exception;

	public CaptionTagResolver(ParserException exception) {
		this.exception = exception;
	}

	@Override
	public @Nullable Tag resolve(@NotNull String name) {
		for (CaptionVariable variable : exception.captionVariables()) {
			if (variable.getKey().equals(name)) return Tag.preProcessParsed(variable.getValue());
		}
		return null;
	}
}
