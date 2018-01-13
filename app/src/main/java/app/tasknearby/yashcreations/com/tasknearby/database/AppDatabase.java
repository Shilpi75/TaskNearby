package app.tasknearby.yashcreations.com.tasknearby.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import app.tasknearby.yashcreations.com.tasknearby.database.dao.LocationDao;
import app.tasknearby.yashcreations.com.tasknearby.database.dao.TaskDao;
import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;

/**
 * @author shilpi
 */

@Database(entities = {TaskModel.class, LocationModel.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    // Add Daos.
    public abstract TaskDao taskDao();

    public abstract LocationDao locationDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DbConstants.APP_DATABASE_NAME)
                    .addMigrations(MIGRATION_2_3)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            DatabaseMigrator.migrateFromSqlToRoom(database);
        }

    };

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
