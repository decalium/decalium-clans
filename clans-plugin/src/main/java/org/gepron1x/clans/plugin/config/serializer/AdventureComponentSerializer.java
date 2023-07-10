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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public final class AdventureComponentSerializer implements ValueSerialiser<Component> {

	private final ComponentSerializer<Component, ? extends Component, String> componentSerializer;

	public AdventureComponentSerializer(ComponentSerializer<Component, ? extends Component, String> componentSerializer) {

		this.componentSerializer = componentSerializer;
	}

	@Override
	public Class<Component> getTargetClass() {
		return Component.class;
	}

	@Override
	public Component deserialise(FlexibleType flexibleType) throws BadValueException {
		return componentSerializer.deserialize(flexibleType.getString());
	}

	@Override
	public String serialise(Component value, Decomposer decomposer) {
		return componentSerializer.serialize(value);
	}
}
