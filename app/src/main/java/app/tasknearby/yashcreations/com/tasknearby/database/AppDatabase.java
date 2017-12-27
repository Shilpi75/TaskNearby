package app.tasknearby.yashcreations.com.tasknearby.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import app.tasknearby.yashcreations.com.tasknearby.constants.DbConstants;
import app.tasknearby.yashcreations.com.tasknearby.database.dao.AttachmentDao;
import app.tasknearby.yashcreations.com.tasknearby.database.dao.LocationDao;
import app.tasknearby.yashcreations.com.tasknearby.database.dao.TaskDao;
import app.tasknearby.yashcreations.com.tasknearby.models.Attachment;
import app.tasknearby.yashcreations.com.tasknearby.models.Location;
import app.tasknearby.yashcreations.com.tasknearby.models.Task;

/**
 * @author shilpi
 */

@Database(entities = {Task.class, Location.class, Attachment.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    // Add Daos.
    public abstract TaskDao taskDao();

    public abstract LocationDao locationDao();

    public abstract AttachmentDao attachmentDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DbConstants.APP_DATABASE_NAME).build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
