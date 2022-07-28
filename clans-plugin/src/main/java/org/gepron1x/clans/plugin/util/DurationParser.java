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
package org.gepron1x.clans.plugin.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class DurationParser {


    public DurationParser(Map<Character, TimeUnit> units) {
        this.units = units;
    }



    private final Map<Character, TimeUnit> units;
    public long parseToSeconds(final String duration) throws ParseException {

        long total = 0;
        final NumberFormat nf = NumberFormat.getInstance();
        final ParsePosition parsePosition = new ParsePosition(0);

        // Consume any starting whitespace of the string
        consumeWhitespace(duration, parsePosition);

        do {
            // Parse the number
            final Number numberOb = nf.parse(duration, parsePosition);
            if (numberOb == null)
                throw new ParseException("Unable to parse number.", parsePosition.getIndex());
            final double number = numberOb.doubleValue();

            // Extract the suffix
            if (duration.length() <= parsePosition.getIndex())
                throw new ParseException("Number '" + number + "' must be followed by a suffix.", parsePosition.getIndex());
            final char suffix = duration.charAt(parsePosition.getIndex());
            TimeUnit unit = units.get(suffix);
            if(unit == null) {
                throw new ParseException("unknown suffix: " + suffix, parsePosition.getIndex());
            }
            total += Math.round(TimeUnit.SECONDS.convert(Math.round(number), unit));

            // Advance and consume whitespace
            parsePosition.setIndex(parsePosition.getIndex() + 1);
            consumeWhitespace(duration, parsePosition);

        } while (parsePosition.getIndex() < duration.length());

        return total;
    }

    /**
     * Advance the parsePosition object to consume all whitespace characters from the current position.
     *
     * @param s             the string to inspect
     * @param parsePosition the current parse position identifying the index from which to start consuming
     */
    private static void consumeWhitespace(final String s, final ParsePosition parsePosition) {
        while (parsePosition.getIndex() < s.length() && Character.isWhitespace(s.charAt(parsePosition.getIndex())))
            parsePosition.setIndex(parsePosition.getIndex() + 1);
    }
}
