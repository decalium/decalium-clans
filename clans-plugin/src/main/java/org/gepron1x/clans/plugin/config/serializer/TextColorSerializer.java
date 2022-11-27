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

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public final class TextColorSerializer implements ValueSerialiser<TextColor> {
    @Override
    public Class<TextColor> getTargetClass() {
        return TextColor.class;
    }

    @Override
    public TextColor deserialise(FlexibleType flexibleType) throws BadValueException {
        String str = flexibleType.getString();
        TextColor color = NamedTextColor.NAMES.value(str);
        if(color != null) return color;
        if(!str.startsWith("#")) throw flexibleType.badValueExceptionBuilder().message("Invalid text color format").build();
        return TextColor.fromHexString(str);
    }

    @Override
    public Object serialise(TextColor value, Decomposer decomposer) {
        return value.toString();
    }
}
