package org.gepron1x.clans.util;


import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Converts duration strings containing a mixture of time units with suffixes, e.g. "2h 30m", into a total number of seconds.
 */
public final class DurationParser {

    /**
     * Private constructor.
     * @param units
     */
    public DurationParser(Map<Character, TimeUnit> units) {
        this.units = units;
    }

    /**
     * Used for readability in other constants.
     */
    private static final int SECONDS = 1;

    /**
     * Number of seconds in a minute.
     */
    private static final int MINUTES = 60 * SECONDS;

    /**
     * Number of seconds in an hour.
     */
    private static final int HOURS = 60 * MINUTES;

    /**
     * Number of seconds in a day.
     */
    private static final int DAYS = 24 * HOURS;

    /**
     * Parses the specified duration string to a total number of seconds.
     * The string must be numbers followed by a suffix, separated by zero or more whitespace characters, e.g. "2h 30m".
     * Allowed suffixes are 'd', 'h', 'm' and 's'.
     * Decimals are supported, but the final result is rounded to the nearest second.
     *
     * @param duration a duration string to convert
     * @return the total number of seconds represented by the duration string
     * @throws ParseException if the input string could not be successfully parsed
     *
     */
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
            total += Math.round(TimeUnit.SECONDS.convert(Math.round(number), units.get(suffix)));

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
