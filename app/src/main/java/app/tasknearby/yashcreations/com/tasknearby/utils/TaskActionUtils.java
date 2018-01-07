package app.tasknearby.yashcreations.com.tasknearby.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

import java.util.Date;

import app.tasknearby.yashcreations.com.tasknearby.TaskRepository;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;

/**
 * Contains actions performed commonly on tasks. Both AlarmActivity and Notifications will be
 * using this.
 *
 * @author vernmyash8
 */
public final class TaskActionUtils {

    public static void onTaskMarkedDone(@NonNull Context appContext, TaskModel task) {
        task.setLastTriggered(new LocalDate());
        task.setIsDone(1);
        TaskRepository repository = new TaskRepository(appContext);
        repository.updateTask(task);
    }

    public static void onTaskSnoozed(@NonNull Context appContext, TaskModel task) {
        task.setSnoozedAt(System.currentTimeMillis());
        task.setLastTriggered(new LocalDate());
        TaskRepository repository = new TaskRepository(appContext);
        repository.updateTask(task);
    }

    public static void setAsNotificationOnly(Context appContext, TaskModel task) {
        task.setIsAlarmSet(0);
        TaskRepository repository = new TaskRepository(appContext);
        repository.updateTask(task);
    }
}
