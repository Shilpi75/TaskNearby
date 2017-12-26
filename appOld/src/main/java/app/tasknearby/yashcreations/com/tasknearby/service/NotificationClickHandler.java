package app.tasknearby.yashcreations.com.tasknearby.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;

import app.tasknearby.yashcreations.com.tasknearby.Constants;
import app.tasknearby.yashcreations.com.tasknearby.R;
import app.tasknearby.yashcreations.com.tasknearby.database.TasksContract;

/**
 * Created by yash on 28/8/17.
 */

public class NotificationClickHandler extends IntentService {

    private static final String TAG = NotificationClickHandler.class.getSimpleName();

    public NotificationClickHandler() {
        super("NotificationClickHandler");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int action = intent.getIntExtra(Constants.NOTIFICATION_BUTTON_ACTION, 0);
        String taskId = intent.getStringExtra(Constants.TaskID);
        if (action == 1) {
            markAsDone(taskId);
        } else if (action == 2) {
            snooze(taskId);
        } else {
            Log.w(TAG, "Unknown action", new UnsupportedOperationException("Unknown notification action."));
        }
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    public void markAsDone(String taskId) {
        Log.d(TAG, "Marking as done " + taskId);
        ContentValues taskValues = new ContentValues();
        taskValues.put(TasksContract.TaskEntry.COLUMN_DONE_STATUS, "true");
        getContentResolver().update(TasksContract.TaskEntry.CONTENT_URI,
                taskValues, TasksContract.TaskEntry._ID + "=?",
                new String[]{taskId});
        Log.i(TAG, "Marked as done " + taskId);
    }

    public void snooze(String taskId) {
        Log.d(TAG, "Snoozing " + taskId);
        ContentValues taskValues = new ContentValues();
        taskValues.put(TasksContract.TaskEntry.COLUMN_SNOOZE_TIME, System.currentTimeMillis());
        getContentResolver().update(TasksContract.TaskEntry.CONTENT_URI,
                taskValues, TasksContract.TaskEntry._ID + "=?",
                new String[]{taskId});
        Log.i(TAG, "Successfully snoozed.");
    }
}
