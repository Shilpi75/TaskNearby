package app.tasknearby.yashcreations.com.tasknearby.utils;

import android.content.Context;
import android.util.Log;

import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.TaskRepository;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;

/**
 * Inserts a random task in the database 10 seconds after being called.
 * It is being used to check if the live data updates are working properly and adapter
 * properly takes in the new data.
 */
public class DbUpdatesSimulator extends Thread {

    private static final String TAG = DbUpdatesSimulator.class.getSimpleName();

    private static final int TIME_BEFORE_STARTING_FIRST_PROCESSING = 3 * 1000;
    private static final int PROCESSING_TIME_PER_TASK_MILLIS = 1;
    private static final int LOCATION_UPDATE_INTERVAL = 2000;
    private static final int NUMBER_OF_TASKS = 500;
    private static final int NUMBER_OF_LOCATION_UPDATES = 500;

    private Context mAppContext;
    private TaskRepository mTaskRepository;

    public DbUpdatesSimulator(Context appContext, TaskRepository taskRepository) {
        mAppContext = appContext;
        mTaskRepository = taskRepository;
    }

    @Override
    public void run() {
        try {
            simulate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void simulate() throws InterruptedException {

        Log.i(TAG, "Simulator Thread going to sleep.");
        Thread.sleep(TIME_BEFORE_STARTING_FIRST_PROCESSING);
        Log.e(TAG, "Starting updates.");
        TaskModel taskModel = new TaskModel.Builder(mAppContext, "Live1", 2)
                .build();
        mTaskRepository.saveTask(taskModel);

        // get anyone task to update
        List<TaskModel> allTasksInDb = mTaskRepository.getAllTasks();
        if (allTasksInDb.size() == 0) {
            return;
        }
        taskModel = allTasksInDb.get(0);
        // Each batch signifies onLocationChanged.
        for (int j = 0; j < NUMBER_OF_LOCATION_UPDATES; j++) {
            // Process the tasks for this batch.
            for (int i = 0; i < NUMBER_OF_TASKS; ++i) {
                // Processing one task.
                taskModel.setLastDistance(i);
                mTaskRepository.updateTask(taskModel);
                Thread.sleep(PROCESSING_TIME_PER_TASK_MILLIS);
            }
            Thread.sleep(LOCATION_UPDATE_INTERVAL);
        }
    }
}
