package app.tasknearby.yashcreations.com.tasknearby.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.joda.time.LocalTime;

import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.R;
import app.tasknearby.yashcreations.com.tasknearby.TaskRepository;
import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;
import app.tasknearby.yashcreations.com.tasknearby.utils.AppUtils;

/**
 * Receives location result callbacks and check for tasks for which an action has to be taken.
 *
 * @author shilpi
 */
public class LocationResultCallback extends LocationCallback {

    private Context mContext;
    private TaskRepository mTaskRepository;

    public LocationResultCallback(Context context) {
        mContext = context;
        mTaskRepository = new TaskRepository(context);
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);

        // Get current time.
        LocalTime currentTime = new LocalTime();

        // Get the current location.
        Location currentLocation = locationResult.getLastLocation();

        // Get the snooze time from settings.
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences
                (mContext);
        long snoozeTime = Long.parseLong(defaultPref.getString(mContext.getString((R.string
                .pref_snooze_time_key)), mContext.getString(R.string.pref_snooze_time_default)));

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
            float lastDistance = getDistance(currentLocation, task);
            // Set the last distance.
            task.setLastDistance(lastDistance);

            if (lastDistance <= task.getReminderRange()) {

                long lastSnoozedTime = task.getSnoozedAt();

                // When to notify/alarm the user: If task is not snoozed yet OR when snoozed task
                // is eligible to ring again.
                // Check is it a snoozed task.
                boolean isSnoozedTask = AppUtils.isSnoozed(lastSnoozedTime);

                if (!isSnoozedTask || AppUtils.isSnoozedTaskEligible(lastSnoozedTime, snoozeTime)) {
                    // Check if alarm is allowed to ring.
                    if (task.getIsAlarmSet() == 1) {
                        // TODO: Ring Alarm.

                    } else {
                        // TODO: No alarm, only notification.

                    }
                }
            }

            // Update task to update last distance.
            mTaskRepository.updateTask(task);
        }
    }

    /**
     * Returns the distance of given Location from the task location.
     *
     * @param currentLocation
     * @param task
     * @return
     */
    float getDistance(Location currentLocation, TaskModel task) {
        LocationModel location = mTaskRepository.getLocationById(task.getLocationId());
        Location taskLocation = new Location(location.getPlaceName());
        taskLocation.setLatitude(location.getLatitude());
        taskLocation.setLongitude(location.getLongitude());
        return currentLocation.distanceTo(taskLocation);
    }
};

