package app.tasknearby.yashcreations.com.tasknearby.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.models.Task;

/**
 * @author shilpi
 */

@Dao
public interface TaskDao {

    @Insert
    public long insertTask(Task task);

    @Insert
    public List<Long> insertTasks(List<Task> taskList);

    @Update
    public void updateTask(Task task);

    @Delete
    public void deleteTask(Task task);

    @Query("SELECT * FROM tasks")
    public List<Task> getAllTasks();

    @Query("SELECT * FROM  tasks WHERE id = :taskId")
    public Task getTaskWithId(long taskId);
}
