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

import com.google.common.collect.ImmutableBiMap;
import org.gepron1x.clans.plugin.util.DurationParser;
import org.gepron1x.clans.plugin.util.MapOf;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

public class DurationSerializer implements ValueSerialiser<Duration> {
    private final DurationParser parser;
    private static final List<TimeUnit> UNITS = List.of(DAYS, HOURS, MINUTES, SECONDS);;
    private static final ImmutableBiMap<Character, TimeUnit> UNIT_CHARACTERS = ImmutableBiMap.copyOf(
            new MapOf<>(unit -> unit.name().toLowerCase(Locale.ROOT).charAt(0), UNITS).create()
    );

    public DurationSerializer() {
        parser = new DurationParser(UNIT_CHARACTERS);
    }
    @Override
    public Class<Duration> getTargetClass() {
        return Duration.class;
    }

    @Override
    public Duration deserialise(FlexibleType flexibleType) throws BadValueException {
        try {
            return Duration.ofSeconds(parser.parseToSeconds(flexibleType.getString()));
        } catch (ParseException e) {
            throw flexibleType.badValueExceptionBuilder().message("invalid duration syntax!").cause(e).build();
        }
    }

    @Override
    public String serialise(Duration duration, Decomposer decomposer) {
        StringBuilder sb = new StringBuilder();
        long seconds = duration.toSeconds();
        for(TimeUnit unit : UNITS) {
            long value = unit.convert(seconds, SECONDS);
            if(value > 0) sb.append(value).append(UNIT_CHARACTERS.inverse().get(unit));
            seconds = seconds - unit.toSeconds(value); // Duration.ofSeconds(duration.getSeconds() - unit.toSeconds(value));
        }
        return sb.toString();
    }
}