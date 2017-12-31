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
    long insertTask(Task task);

    @Insert
    List<Long> insertTasks(List<Task> taskList);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Query("SELECT * FROM  tasks WHERE id = :taskId")
    Task getTaskWithId(long taskId);
}
