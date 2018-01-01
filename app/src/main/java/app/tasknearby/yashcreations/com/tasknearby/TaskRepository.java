package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Context;
import android.util.Log;

import org.joda.time.LocalTime;

import java.util.Date;
import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.database.AppDatabase;
import app.tasknearby.yashcreations.com.tasknearby.database.DbConstants;
import app.tasknearby.yashcreations.com.tasknearby.models.Attachment;
import app.tasknearby.yashcreations.com.tasknearby.models.Location;
import app.tasknearby.yashcreations.com.tasknearby.models.Task;

/**
 * Handles all data operations
 *
 * @author shilpi on 27/12/17.
 */

public class TaskRepository {

    private static final String TAG = TaskRepository.class.getSimpleName();

    private Context mContext;

    public TaskRepository(Context context) {
        this.mContext = context;
    }

    /**
     * Fetches all the tasks from database.
     */
    public List<Task> getAllTasks() {
        return AppDatabase.getAppDatabase(mContext).taskDao().getAllTasks();
    }

    /**
     * Fetches the task with the given id.
     */
    public Task getTaskWithId(long taskId) {
//        return AppDatabase.getAppDatabase(mContext).taskDao().getTaskWithId(taskId);
        // TODO: After database starts working, remove this mock data call.
        return new Task("Check reception plannings", 0, null, 0,
                0, 70, 0, new LocalTime(10, 20),
                new LocalTime(17, 00), new Date(1000000), new Date(), new Date(),
                DbConstants.REPEAT_DAILY, DbConstants.ANYTHING, DbConstants.BOTH_ENTER_EXIT,
                120, null, 0L, new Date());
    }

    /**
     * Returns a location object with the given id from the database.
     */
    public Location getLocationById(long locationId) {
//        return AppDatabase.getAppDatabase(mContext).locationDao().getLocationWithId(locationId);
        return new Location("Hyatt Residency, New Delhi, 110042", "23.0", "77.0",
                1, 0, new Date());
    }

    public Attachment getAttachmentById(long attachmentId) {
//      return AppDatabase.getAppDatabase(mContext).attachmentDao().getAttachmentWithId(attachmentId);
        return new Attachment("Check for music, food and creatives also.");
    }

    /**
     * Updates the task with taskId of the task passed as param.
     */
    public void updateTask(Task task) {
        Log.i(TAG, "Update called for task: " + task.getTaskName());
        AppDatabase.getAppDatabase(mContext).taskDao().updateTask(task);
    }

    /**
     * Deletes the task from the database.
     */
    public void removeTask(Task task) {
        Log.i(TAG, "Requested deletion of task: " + task.getTaskName());
        AppDatabase.getAppDatabase(mContext).taskDao().deleteTask(task);
    }
}
