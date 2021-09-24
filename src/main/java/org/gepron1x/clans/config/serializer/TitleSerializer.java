package org.gepron1x.clans.config.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.gepron1x.clans.util.Configs;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TitleSerializer implements ValueSerialiser<Title> {
    private static final String TITLE = "title", SUBTITLE = "subtitle", TIMES = "times";


    @Override
    public Class<Title> getTargetClass() {
        return Title.class;
    }

    @Override
    public Title deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, FlexibleType> map = flexibleType.getMap(Configs.section());
        Component title = map.get(TITLE).getObject(Component.class);
        Component subtitle = map.get(SUBTITLE).getObject(Component.class);
        String times = map.get(TIMES).getString();
        String[] temp = times.split(", ");
        Title.Times titleTimes = Title.Times.of(
                Duration.ofSeconds(Long.parseLong(temp[0])),
                Duration.ofSeconds(Long.parseLong(temp[1])),
                Duration.ofSeconds(Long.parseLong(temp[2]))
        );

        return Title.title(title, subtitle, titleTimes);
    }

    @Override
    public Map<String, Object> serialise(Title title, Decomposer decomposer) {
        Map<String, Object> map = new HashMap<>(3);
        map.put(TITLE, decomposer.decompose(Component.class, title.title()));
        map.put(SUBTITLE, decomposer.decompose(Component.class, title.subtitle()));
        Title.Times times = title.times();
        String rawTimes = String.join(", ",
                String.valueOf(times.fadeIn().toSeconds()),
                String.valueOf(times.stay().toSeconds()),
                String.valueOf(times.fadeOut().toSeconds()));
        map.put(TIMES, rawTimes);
        return map;
    }
}
