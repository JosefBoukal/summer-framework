package org.summerframework.util;

/**
 * The duration utility class.
 */
public abstract class DurationUtils {

    private static final String[] MILLI_UNITS = new String[]{" ms", " sec", " min", " h", " d"};
    private static final long[] MILL_MODULI = new long[]{1000, 60, 60, 24};

    private static final String[] MICRO_UNITS = new String[]{" µs", " ms", " sec", " min", " h", " d"};
    private static final long[] MICRO_MODULI = new long[]{1000, 1000, 60, 60, 24};

    /**
     * This method returns the given <code>time</code> using milliseconds, seconds, minutes, hours and days. For
     * example, given 1234 it returns "1 sec 234 ms". Any part with 0 value is skipped (not printed).
     *
     * @param milliseconds the time interval in milliseconds
     */
    public static String milliDuration(long milliseconds) {
        if (milliseconds == 0) {
            return "0 ms";
        }
        return duration(milliseconds, MILLI_UNITS, MILL_MODULI);
    }

    /**
     * This method returns duration of the given nanoseconds as microseconds, milliseconds, seconds, minutes, hours and
     * days. For example, given 1234001000 it returns "1 sec 234 ms 1 µs". Any part with 0 value is skipped (not
     * printed).
     *
     * @param nanoseconds the time interval in nanoseconds
     */
    public static String microDuration(long nanoseconds) {
        long microseconds = nanoseconds / 1000;
        if (microseconds == 0) {
            return "0 µs";
        }
        return duration(microseconds, MICRO_UNITS, MICRO_MODULI);
    }

    private static String duration(long duration, String[] units, long[] moduli) {
        // normalize to positive duration
        duration = Math.abs(duration);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < units.length; i++) {
            long value;
            if (i < moduli.length) {
                long mod = moduli[i];
                value = duration % mod;
                duration = duration / mod;
            } else {
                value = duration;
            }
            if (value != 0) {
                if (result.length() > 0) {
                    result.insert(0, ' ');
                }
                result.insert(0, units[i]);
                result.insert(0, value);
            }
            if (duration == 0) {
                break;
            }
        }
        return result.toString();
    }

}
