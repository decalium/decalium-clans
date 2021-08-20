package org.gepron1x.clans.config.serializer;

import com.google.common.collect.ImmutableBiMap;
import org.gepron1x.clans.util.DurationParser;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

public class DurationSerializer implements ValueSerialiser<Duration> {
    private final DurationParser parser;
    private static final List<TimeUnit> UNITS = List.of(DAYS, HOURS, MINUTES, SECONDS);
    private static final ImmutableBiMap<Character, TimeUnit> UNIT_CHARACTERS =
            new ImmutableBiMap.Builder<Character, TimeUnit>()
                    .put('d', DAYS)
                    .put('h', HOURS)
                    .put('m', MINUTES)
                    .put('s', SECONDS)
                    .build();
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
        for(TimeUnit unit : UNITS) {
            long value = unit.convert(duration);
            if(value > 0) sb.append(value).append(UNIT_CHARACTERS.inverse().get(unit));
            duration = Duration.ofSeconds(duration.getSeconds() - unit.toSeconds(value));
        }
        return sb.toString();
    }
}
