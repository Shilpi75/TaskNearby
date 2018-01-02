package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.database.AppDatabase;
import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;

/**
 * Handles all data operations
 *
 * @author shilpi on 27/12/17.
 */

public class TaskRepository {

    private static final String TAG = TaskRepository.class.getSimpleName();

    /**
     * AppDatabase instance.
     */
    private AppDatabase mDatabase;

    /**
     * Constructor for getting the instance of AppDatabase using context.
     *
     * @param context using which AppDatabase will be retrieved.
     */
    public TaskRepository(Context context) {
        mDatabase = AppDatabase.getAppDatabase(context);
        // TODO: Remove mock data call.
        dummyTask = new TaskModel.Builder(context, "Check reception plannings", 0)
                .setIsAlarmSet(0)
                .setIsDone(1)
                .setStartDate(new Date(12200000))
                .setEndDate(new Date(34))
                .setNote("This is a note")
                .build();
    }

    /**
     * Fetches all the tasks from database.
     */
    public List<TaskModel> getAllTasks() {
          return mDatabase.taskDao().getAllTasks();
//         TODO: Remove mock data.
//        return Arrays.asList(dummyTask, dummyTask);
    }

    /**
     * Fetches the task with the given id.
     */
    public TaskModel getTaskWithId(long taskId) {
        // return mDatabase.taskDao().getTaskWithId(taskId);
        // TODO: After database starts working, remove this mock data call.
        return dummyTask;
    }

    /**
     * Saves the task to the database.
     *
     * @return the id of the saved task.
     */
    public long saveTask(TaskModel task) {
        return mDatabase.taskDao().insertTask(task);
    }

    /**
     * Updates the task with taskId of the task passed as param.
     */
    public void updateTask(TaskModel task) {
        Log.i(TAG, "Update called for task: " + task.getTaskName());
        mDatabase.taskDao().updateTask(task);
    }

    /**
     * Deletes the task from the database.
     */
    public void removeTask(TaskModel task) {
        Log.i(TAG, "Requested deletion of task: " + task.getTaskName());
        mDatabase.taskDao().deleteTask(task);
    }

    /**
     * Returns a location object with the given id from the database.
     */
    public LocationModel getLocationById(long locationId) {
        // return mDatabase.locationDao().getLocationWithId(locationId);
        return mockLocationModel;
    }

    /**
     * Returns all locations present in the database.
     */
    public List<LocationModel> getAllLocations() {
        return mDatabase.locationDao().getAllLocations();
    }

    /**
     * Saves the location to the database.
     */
    public long saveLocation(LocationModel locationModel) {
        return mDatabase.locationDao().insertLocation(locationModel);
    }

    /**
     * Mock task which will be used for debugging.
     */
    private TaskModel dummyTask;
    private LocationModel mockLocationModel = new LocationModel("Hyatt Residency, New Delhi, 110042", 23.0,
            77.0, 1, 0, new Date());
}
