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
package org.gepron1x.clans.plugin.util;

import org.gepron1x.clans.plugin.config.format.TimeFormat;

import java.time.Duration;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class BaseTimeFormat implements TimeFormat {

    private final SortedMap<TimeUnit, String> units;

    public BaseTimeFormat(SortedMap<TimeUnit, String> units) {

        this.units = units;
    }
    @Override
    public String format(Duration duration) {
        long seconds = duration.toSeconds();
        if(seconds == 0) return "0" + units.getOrDefault(units.lastKey(), "");
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<TimeUnit, String> entry : units.entrySet()) {
            long value = entry.getKey().convert(seconds, SECONDS);
            if(value > 0) sb.append(value).append(entry.getValue());
            seconds = seconds - entry.getKey().toSeconds(value); // Duration.ofSeconds(duration.getSeconds() - unit.toSeconds(value));
        }
        return sb.toString();
    }

    public Map<TimeUnit, String> units() {
        return units;
    }
}
