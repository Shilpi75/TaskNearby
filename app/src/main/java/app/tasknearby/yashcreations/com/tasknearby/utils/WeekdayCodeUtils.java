package app.tasknearby.yashcreations.com.tasknearby.utils;

import java.util.Calendar;

/**
 * Provides codes for weekdays for setting repeatable reminders' days.
 */
public class WeekdayCodeUtils {

    private static final int DAY_CODE_MONDAY = 1;
    private static final int DAY_CODE_TUESDAY = 2;
    private static final int DAY_CODE_WEDNESDAY = 4;
    private static final int DAY_CODE_THURSDAY = 8;
    private static final int DAY_CODE_FRIDAY = 16;
    private static final int DAY_CODE_SATURDAY = 32;
    private static final int DAY_CODE_SUNDAY = 64;

    /**
     * Used for getting day code by passing in the index of the day.
     */
    private static final int[] DAY_CODES_ARRAY = {
            DAY_CODE_MONDAY,
            DAY_CODE_TUESDAY,
            DAY_CODE_WEDNESDAY,
            DAY_CODE_THURSDAY,
            DAY_CODE_FRIDAY,
            DAY_CODE_SATURDAY,
            DAY_CODE_SUNDAY
    };

    /**
     * Returns dayCode for the day index passed to this function. Assumes MONDAY is the first day
     * of the week.
     */
    public static int getDayCodeByIndex(int index) {
        // MONDAY is = 1 in input. So, get the (index - 1)th item.
        return DAY_CODES_ARRAY[index - 1];
    }

    /**
     * Returns dayCode for the calendar day index passed to this function assuming SUNDAY as the
     * first day
     * of the week. (Just like Java's calendar API.)
     */
    public static int getDayCodeByCalendarDayId(int calendarDayId) {
        int ourDayIndex = 7;
        if (calendarDayId != Calendar.SUNDAY) {
            ourDayIndex = calendarDayId - 1;
        }
        return getDayCodeByIndex(ourDayIndex);
    }

}
