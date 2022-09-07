/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.config.serializer;

import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.regex.Pattern;

public final class PatternSerializer implements ValueSerialiser<Pattern> {
    @Override
    public Class<Pattern> getTargetClass() {
        return Pattern.class;
    }

    @Override
    public Pattern deserialise(FlexibleType flexibleType) throws BadValueException {
        try {
            return Pattern.compile(flexibleType.getString());
        } catch (IllegalArgumentException e) {
            throw flexibleType.badValueExceptionBuilder().cause(e).message("Invalid pattern syntax. Try it on https://regex101.com/ first.").build();
        }

    }

    @Override
    public Object serialise(Pattern value, Decomposer decomposer) {
        return value.toString();
    }
}
