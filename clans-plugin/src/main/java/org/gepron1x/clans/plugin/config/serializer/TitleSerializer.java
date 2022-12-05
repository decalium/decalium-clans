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

import com.google.common.base.Splitter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

public final class TitleSerializer implements ValueSerialiser<Title> {

    private static final Splitter SPLITTER = Splitter.on(Pattern.compile(", ?"));

    private final byte MILLIS_PER_TICK = 50;

    private static final String TITLE = "title", SUBTITLE = "subtitle", TIMES = "times";
    @Override
    public Class<Title> getTargetClass() {
        return Title.class;
    }

    @Override
    public Title deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, FlexibleType> map = flexibleType.getMap((key, value) -> Map.entry(key.getString(), value));
        Component title = map.get(TITLE).getObject(Component.class);
        Component subtitle = map.get(SUBTITLE).getObject(Component.class);
        FlexibleType timesRaw = map.get(TIMES);
        Title.Times times = timesRaw == null ? Title.DEFAULT_TIMES : parseTimes(timesRaw.getString());
        return Title.title(title, subtitle, times);
    }

    private Title.Times parseTimes(String times) {
        @SuppressWarnings("UnstableApiUsage")
        List<Duration> durations = SPLITTER.splitToStream(times)
                .map(s -> Duration.ofMillis(Long.parseLong(s) * MILLIS_PER_TICK))
                .toList();

        return Title.Times.times(durations.get(0), durations.get(1), durations.get(2));
    }

    private String serialiseTimes(Title.Times times) {
        StringJoiner joiner = new StringJoiner(", ");
        for(Duration duration : Arrays.asList(times.fadeIn(), times.stay(), times.fadeOut())) {
            joiner.add(String.valueOf((int) duration.toMillis() / MILLIS_PER_TICK));
        }
        return joiner.toString();
    }

    @Override
    public Map<String, Object> serialise(Title value, Decomposer decomposer) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(TITLE, decomposer.decompose(Component.class, value.title()));
        map.put(SUBTITLE, decomposer.decompose(Component.class, value.subtitle()));
        Title.Times times = value.times();
        if(times != null && !Title.DEFAULT_TIMES.equals(times)) {
            map.put(TIMES, serialiseTimes(times));
        }
        return map;
    }
}
