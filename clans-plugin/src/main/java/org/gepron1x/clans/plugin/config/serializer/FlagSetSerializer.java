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

import org.gepron1x.clans.plugin.wg.FlagSet;
import org.gepron1x.clans.plugin.wg.StringFlagSet;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.Map;

public final class FlagSetSerializer implements ValueSerialiser<FlagSet> {
    @Override
    public Class<FlagSet> getTargetClass() {
        return FlagSet.class;
    }

    @Override
    public FlagSet deserialise(FlexibleType flexibleType) throws BadValueException {
        return new StringFlagSet(flexibleType.getMap((key, value) -> Map.entry(key.getString(), value.getString())));
    }

    @Override
    public Object serialise(FlagSet value, Decomposer decomposer) {
        return value.serialize();
    }
}
