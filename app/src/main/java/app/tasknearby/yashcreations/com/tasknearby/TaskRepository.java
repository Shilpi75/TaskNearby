package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Context;

import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.database.AppDatabase;
import app.tasknearby.yashcreations.com.tasknearby.models.Attachment;
import app.tasknearby.yashcreations.com.tasknearby.models.Location;
import app.tasknearby.yashcreations.com.tasknearby.models.Task;

/**
 * Handles all data operations
 * @author shilpi on 27/12/17.
 */

public class TaskRepository {

    private Context context;

    public TaskRepository(Context context){
        this.context = context;
    }

    /**
     * Fetches all the tasks from database.
     */
    public List<Task> getAllTasks(){
        return AppDatabase.getAppDatabase(context).taskDao().getAllTasks();
    }

    /**
     * Fetches the task with the given id.
     */
    public Task getTaskWithId(long taskId){
        return AppDatabase.getAppDatabase(context).taskDao().getTaskWithId(taskId);
    }

}
