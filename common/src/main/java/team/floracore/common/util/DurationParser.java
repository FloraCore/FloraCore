package team.floracore.common.util;

import com.google.common.collect.ImmutableMap;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses durations from a string format
 */
public final class DurationParser {
    private static final Map<ChronoUnit, String> UNITS_PATTERNS = ImmutableMap.<ChronoUnit, String>builder()
                                                                              .put(ChronoUnit.YEARS, "y(?:ear)?s?")
                                                                              .put(ChronoUnit.MONTHS, "mo(?:nth)?s?")
                                                                              .put(ChronoUnit.WEEKS, "w(?:eek)?s?")
                                                                              .put(ChronoUnit.DAYS, "d(?:ay)?s?")
                                                                              .put(ChronoUnit.HOURS, "h(?:our|r)?s?")
                                                                              .put(ChronoUnit.MINUTES,
                                                                                   "m(?:inute|in)?s?")
                                                                              .put(ChronoUnit.SECONDS,
                                                                                   "s(?:econd|ec)?s?")
                                                                              .build();
    private static final ChronoUnit[] UNITS = UNITS_PATTERNS.keySet().toArray(new ChronoUnit[0]);
    private static final String PATTERN_STRING = UNITS_PATTERNS.values().stream()
                                                               .map(pattern -> "(?:(\\d+)\\s*" + pattern + "[,\\s]*)?")
                                                               .collect(Collectors.joining("", "^\\s*", "$"));
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING, Pattern.CASE_INSENSITIVE);

    private DurationParser() {
    }

    public static Duration parseDuration(String input) throws IllegalArgumentException {
        Matcher matcher = PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("unable to parse duration: " + input);
        }

        Duration duration = Duration.ZERO;
        for (int i = 0; i < UNITS.length; i++) {
            ChronoUnit unit = UNITS[i];
            int g = i + 1;

            if (matcher.group(g) != null && !matcher.group(g).isEmpty()) {
                int n = Integer.parseInt(matcher.group(g));
                if (n > 0) {
                    duration = duration.plus(unit.getDuration().multipliedBy(n));
                }
            }
        }

        return duration;
    }

}