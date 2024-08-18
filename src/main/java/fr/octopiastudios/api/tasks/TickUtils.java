package fr.octopiastudios.api.tasks;

import java.util.concurrent.TimeUnit;

public class TickUtils {

    public static int fromMinute(int min) {
        return (int) (TimeUnit.MINUTES.toSeconds(min) * 20);
    }

    public static int fromSecond(int seconds) {
        return seconds * 20;
    }

    public static int fromHour(int hour) {
        return (int) (TimeUnit.HOURS.toSeconds(hour) * 20);
    }
}
