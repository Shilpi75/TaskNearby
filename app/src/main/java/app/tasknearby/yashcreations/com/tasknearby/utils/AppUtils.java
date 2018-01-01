package app.tasknearby.yashcreations.com.tasknearby.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTimeComparator;
import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.tasknearby.yashcreations.com.tasknearby.R;

/**
 * Contains utility functions used throughout the app.
 *
 * @author vermayash8
 */
public final class AppUtils {

    /**
     * Returns a formatted time string in 12-hour format.
     */
    public static String getReadableTime(LocalTime localTime) {
        int hourOfDay = localTime.getHourOfDay();
        int minute = localTime.getMinuteOfHour();
        String periodSuffix = "AM";
        if (hourOfDay > 12) {
            hourOfDay -= 12;
            periodSuffix = "PM";
        } else if (hourOfDay == 12) {
            periodSuffix = "PM";
        }
        return String.format(Locale.ENGLISH, "%02d:%02d %s", hourOfDay, minute, periodSuffix);
    }

    /**
     * Returns dates as "Today", "Fri, 12 Dec 17" or "Forever" (null) for the corresponding input
     * Date object. Later on, this can be modified to return "Yesterday", "Tomorrow" also.
     */
    public static String getReadableDate(@NonNull Context context, @Nullable Date date) {
        if (date == null) {
            return context.getString(R.string.detail_date_forever);
        } else if (DateTimeComparator.getDateOnlyInstance().compare(date, new Date()) == 0) {
            return context.getString(R.string.detail_date_today);
        } else {
            SimpleDateFormat sdfReadable = new SimpleDateFormat("EEE, d MMM yy", Locale.ENGLISH);
            return sdfReadable.format(date);
        }
    }

}
