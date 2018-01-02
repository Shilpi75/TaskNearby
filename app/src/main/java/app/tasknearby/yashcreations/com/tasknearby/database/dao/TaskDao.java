package app.tasknearby.yashcreations.com.tasknearby.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;

/**
 * @author shilpi
 */
@Dao
public interface TaskDao {

    @Insert
    long insertTask(TaskModel task);

    @Insert
    List<Long> insertTasks(List<TaskModel> tasks);

    @Update
    void updateTask(TaskModel task);

    @Delete
    void deleteTask(TaskModel task);

    @Query("SELECT * FROM tasks")
    List<TaskModel> getAllTasks();

    @Query("SELECT * FROM  tasks WHERE id = :taskId")
    TaskModel getTaskWithId(long taskId);
}
