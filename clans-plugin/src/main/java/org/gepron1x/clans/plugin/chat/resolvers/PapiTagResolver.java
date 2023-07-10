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
package org.gepron1x.clans.plugin.chat.resolvers;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PapiTagResolver implements TagResolver {

	private final OfflinePlayer player;

	public PapiTagResolver(@Nullable OfflinePlayer player) {

		this.player = player;
	}


	private boolean isPlaceholder(String name) {
		return name.equals("papi");
	}

	@Override
	public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
		String str = arguments.popOr("Define the placeholder you wanna to use.").value();
		String percents = "%" + str + "%";
		String result = PlaceholderAPI.setPlaceholders(this.player, percents);
		if (result.equals(percents)) return null;
		Component component;
		if (result.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1) {
			component = LegacyComponentSerializer.legacyAmpersand().deserialize(result);
		} else {
			component = Component.text(result);
		}

		return Tag.inserting(component);
	}

	@Override
	public boolean has(@NotNull String name) {
		return isPlaceholder(name);
	}
}
