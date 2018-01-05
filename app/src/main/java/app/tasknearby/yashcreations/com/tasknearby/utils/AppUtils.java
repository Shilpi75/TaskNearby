package app.tasknearby.yashcreations.com.tasknearby.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.Task;

import org.joda.time.DateTimeComparator;
import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.tasknearby.yashcreations.com.tasknearby.R;
import app.tasknearby.yashcreations.com.tasknearby.TaskRepository;
import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;

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

    public static boolean isSnoozed(long lastSnoozedTime) {
        return (lastSnoozedTime != -1);
    }

    public static boolean isTaskActiveAtTime(TaskModel task, LocalTime time) {
        LocalTime startTime = task.getStartTime();
        LocalTime endTime = task.getEndTime();
        return ((startTime.compareTo(time) <= 0) && (endTime.compareTo(time) >= 0));
    }

    public static boolean isSnoozedTaskEligible(long lastSnoozedTime, long snoozeTime) {
        return (lastSnoozedTime + snoozeTime <= System.currentTimeMillis());
    }

    public static void sendFeedbackEmail(Context context) {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
        mailIntent.setType("text/plain");
        mailIntent.setData(Uri.parse("mailto:"));
        mailIntent.putExtra(Intent.EXTRA_EMAIL, context.getResources().getStringArray(R.array.email_ids));
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject));
        context.startActivity(mailIntent);
    }

    public static void rateApp(Context context) {
        String packageName = context.getPackageName();
        String appUrl = context.getString(R.string.play_store_base_url) + packageName;
        try {
            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.rating_base_url) +
                    packageName));
            context.startActivity(rateIntent);
        }catch (ActivityNotFoundException e){
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
            context.startActivity(playStoreIntent);
        }
    }
}
