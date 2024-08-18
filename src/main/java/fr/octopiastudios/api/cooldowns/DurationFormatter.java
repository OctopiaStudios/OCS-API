package fr.octopiastudios.api.cooldowns;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DurationFormatter {

    private static final long MINUTE;
    private static final long HOUR;
    private static final long DAY;
    private static final ThreadLocal<DecimalFormat> REMAINING_SECONDS;
    private static final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING;
    private static final SimpleDateFormat FRENCH_DATE_FORMAT;

    public static String getRemaining(final long millis, final boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }

    public static String getRemaining(final long duration, final boolean milliseconds, final boolean trail) {
        if (milliseconds && duration < DurationFormatter.MINUTE) {
            return (trail ? DurationFormatter.REMAINING_SECONDS_TRAILING : DurationFormatter.REMAINING_SECONDS).get().format(duration * 0.001) + 's';
        }
        if (duration >= DurationFormatter.DAY) {
            return DurationFormatUtils.formatDuration(duration, "dd-HH:mm:ss");
        }
        return DurationFormatUtils.formatDuration(duration, ((duration >= DurationFormatter.HOUR) ? "HH:" : "") + "mm:ss");
    }

    public static String getDurationWords(final long duration) {
        return DurationFormatUtils.formatDuration(duration, "d' jours 'H' heures 'm' minutes 's' secondes'");
    }

    public static String getDurationDate(final long duration) {
        return DurationFormatter.FRENCH_DATE_FORMAT.format(new Date(duration));
    }

    public static String getCurrentDate() {
        return DurationFormatter.FRENCH_DATE_FORMAT.format(new Date());
    }

    static {
        MINUTE = TimeUnit.MINUTES.toMillis(1L);
        HOUR = TimeUnit.HOURS.toMillis(1L);
        DAY = TimeUnit.DAYS.toMillis(1L);
        REMAINING_SECONDS = ThreadLocal.withInitial(() -> new DecimalFormat("0.#"));
        REMAINING_SECONDS_TRAILING = ThreadLocal.withInitial(() -> new DecimalFormat("0.0"));
        FRENCH_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }
}
