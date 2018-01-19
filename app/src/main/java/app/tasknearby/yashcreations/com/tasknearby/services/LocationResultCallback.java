package app.tasknearby.yashcreations.com.tasknearby.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.AlarmActivity;
import app.tasknearby.yashcreations.com.tasknearby.R;
import app.tasknearby.yashcreations.com.tasknearby.TaskRepository;
import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;
import app.tasknearby.yashcreations.com.tasknearby.notification.NotificationHelper;
import app.tasknearby.yashcreations.com.tasknearby.utils.AppUtils;
import app.tasknearby.yashcreations.com.tasknearby.utils.DistanceUtils;

/**
 * Receives location result callbacks and check for tasks for which an action has to be taken.
 *
 * @author shilpi
 */
public class LocationResultCallback extends LocationCallback {

    private Context mContext;
    private TaskRepository mTaskRepository;
    private Location mLastLocation;

    private NotificationHelper mNotificationHelper;

    LocationResultCallback(Context context) {
        mContext = context;
        mTaskRepository = new TaskRepository(context);
        mNotificationHelper = new NotificationHelper(mContext.getApplicationContext());
        mLastLocation = null;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        Location currentLocation = locationResult.getLastLocation();
        if (mLastLocation == null || !isLocationSame(currentLocation, mLastLocation)) {
            // Performing all operations on a different thread.
            LocationCallbackRunnable callbackThread = new LocationCallbackRunnable(locationResult);
            new Thread(callbackThread).start();
            mLastLocation = locationResult.getLastLocation();
        }
    }

    private boolean isLocationSame(Location locationA, Location locationB) {
        return (locationA.getLongitude() == locationB.getLongitude() && locationA.getLatitude() ==
                locationB.getLatitude());
    }


    private class LocationCallbackRunnable implements Runnable {

        private LocationResult mLocationResult;

        LocationCallbackRunnable(LocationResult locationResult) {
            mLocationResult = locationResult;
        }

        @Override
        public void run() {
            // Get current time.
            LocalTime currentTime = new LocalTime();

            // Get the current location.
            Location currentLocation = mLocationResult.getLastLocation();

            // A list of tasks to be updated.
            List<TaskModel> tasksToUpdate = new ArrayList<>();

            // Get the snooze time from settings.
            SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences
                    (mContext);
            long snoozeTime = Long.parseLong(defaultPref.getString(mContext.getString((R.string
                    .pref_snooze_time_key)), mContext.getString(R.string
                    .pref_snooze_time_default)));

            // Get all the tasks not marked done and active for today.
            List<TaskModel> tasks = mTaskRepository.getNotDoneTasksForToday();

            // Check for each Task:
            // 1. If it is active in the current time
            // 2. Calculate distance form task's location.
            // 3. Check if last distance is less than the reminder range.
            // 4. Check for snoozed or not. Proceed accordingly.
            // 5. Update the task.
            for (TaskModel task : tasks) {

                // Check if active in current time. If not, continue.
                if (!AppUtils.isTaskActiveAtTime(task, currentTime))
                    continue;

                // Get the distance from task's location.
                LocationModel taskLocation = mTaskRepository.getLocationById(task.getLocationId());
                float lastDistance = DistanceUtils.getDistance(currentLocation, taskLocation);
                // Set the last distance.
                task.setLastDistance(lastDistance);

                if (lastDistance <= task.getReminderRange()) {

                    long lastSnoozedTime = task.getSnoozedAt();

                    // When to notify/alarm the user: If task is not snoozed yet OR when snoozed
                    // task
                    // is eligible to ring again.
                    // Check is it a snoozed task.
                    boolean isSnoozedTask = AppUtils.isSnoozed(lastSnoozedTime);

                    if (!isSnoozedTask || AppUtils.isSnoozedTaskEligible(lastSnoozedTime,
                            snoozeTime)) {
                        // Check if alarm is allowed to ring.
                        if (task.getIsAlarmSet() == 1) {
                            Intent alarmIntent = AlarmActivity.getStartingIntent(mContext,
                                    task.getId());
                            alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(alarmIntent);
                        } else {
                            mNotificationHelper.showReminderNotification(task);
                        }
                    }
                }
                // Add this task to the list of tasks to be updated.
                tasksToUpdate.add(task);
            }

            // Batch update tasks.
            mTaskRepository.updateTasks(tasksToUpdate);
        }


    }
}