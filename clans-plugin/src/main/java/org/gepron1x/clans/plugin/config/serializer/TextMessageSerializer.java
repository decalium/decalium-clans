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
package org.gepron1x.clans.plugin.config.serializer;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.gepron1x.clans.plugin.util.message.TextMessage;
import org.jetbrains.annotations.NotNull;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public final class TextMessageSerializer implements ValueSerialiser<TextMessage> {

	private final MiniMessage miniMessage;

	public TextMessageSerializer(@NotNull MiniMessage miniMessage) {
		this.miniMessage = miniMessage;
	}

	@Override
	public Class<TextMessage> getTargetClass() {
		return TextMessage.class;
	}

	@Override
	public TextMessage deserialise(FlexibleType flexibleType) throws BadValueException {
		return TextMessage.message(flexibleType.getString(), miniMessage);
	}

	@Override
	public String serialise(TextMessage value, Decomposer decomposer) {
		return value.value();
	}
}
