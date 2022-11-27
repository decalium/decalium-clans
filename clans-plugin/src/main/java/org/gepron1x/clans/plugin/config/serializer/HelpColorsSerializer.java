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

import cloud.commandframework.minecraft.extras.MinecraftHelp;
import net.kyori.adventure.text.format.TextColor;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HelpColorsSerializer implements ValueSerialiser<MinecraftHelp.HelpColors> {
    private static final String PRIMARY = "primary", HIGHLIGHT = "highlight",
            ALTERNATE_HIGHLIGHT = "alternate-highlight", TEXT = "text", ACCENT = "accent";

    @Override
    public Class<MinecraftHelp.HelpColors> getTargetClass() {
        return MinecraftHelp.HelpColors.class;
    }

    @Override
    public MinecraftHelp.HelpColors deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, TextColor> map = flexibleType.getMap((key, value) -> Map.entry(key.getString(), value.getObject(TextColor.class)));
        MinecraftHelp.HelpColors defaultColors = MinecraftHelp.DEFAULT_HELP_COLORS;
        return MinecraftHelp.HelpColors.of(
                map.getOrDefault(PRIMARY, defaultColors.primary()),
                map.getOrDefault(HIGHLIGHT, defaultColors.highlight()),
                map.getOrDefault(ALTERNATE_HIGHLIGHT, defaultColors.alternateHighlight()),
                map.getOrDefault(TEXT, defaultColors.text()),
                map.getOrDefault(ACCENT, defaultColors.accent())
        );
    }

    @Override
    public Object serialise(MinecraftHelp.HelpColors value, Decomposer decomposer) {
        Map<String, Object> map = new LinkedHashMap<>(5);
        map.put(PRIMARY, decomposer.decompose(TextColor.class, value.primary()));
        map.put(HIGHLIGHT, decomposer.decompose(TextColor.class, value.highlight()));
        map.put(ALTERNATE_HIGHLIGHT, decomposer.decompose(TextColor.class, value.alternateHighlight()));
        map.put(TEXT, decomposer.decompose(TextColor.class, value.text()));
        map.put(ACCENT, decomposer.decompose(TextColor.class, value.accent()));
        return map;
    }
}
