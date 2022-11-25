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

import org.gepron1x.clans.plugin.config.format.TimeFormat;
import org.gepron1x.clans.plugin.util.BaseTimeFormat;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public final class TimeFormatSerializer implements ValueSerialiser<TimeFormat> {

    @Override
    public Class<TimeFormat> getTargetClass() {
        return TimeFormat.class;
    }

    @Override
    public TimeFormat deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<TimeUnit, String> map = flexibleType.getMap((key, value) -> Map.entry(key.getEnum(TimeUnit.class), value.getString()));
        SortedMap<TimeUnit, String> sorted = new TreeMap<>(Comparator.reverseOrder());
        sorted.putAll(map);
        return new BaseTimeFormat(sorted);
    }

    @Override
    public Object serialise(TimeFormat value, Decomposer decomposer) {
        if(value instanceof BaseTimeFormat format) {
            return decomposer.decomposeMap(TimeUnit.class, String.class, format.units());
        }
        return Map.of();
    }
}
