package app.tasknearby.yashcreations.com.tasknearby.database;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Stores all the database related constants
 * @author shilpi
 */

public class DbConstants {

    /**
     * Name of app database created.
     */
    public static final String APP_DATABASE_NAME = "task-database";

    /**
     * Activity based alarm constants.
     */
    public static final int ANYTHING = 0;
    public static final int WALKING = 1;
    public static final int DRIVING = 2;

    /**
     * Movement type constants
     */
    public static final int BOTH_ENTER_EXIT = 0;
    public static final int ENTER = 1;
    public static final int EXIT = 2;

    /**
     * Repeat type constants
     */
    public static final int NO_REPEAT = 0;
    public static final int DAILY = 1;
    public static final int WEEKLY = 2;
    public static final int MONTHLY = 3;

    /**
     * Annotation for Activity Modes.
     */
    @IntDef({ANYTHING, WALKING, DRIVING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActivityModes { }

    /**
     * Annotation for Movement Types.
     */
    @IntDef({BOTH_ENTER_EXIT, ENTER, EXIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MovementTypes { }

    /**
     * Annotation for Repeat Types.
     */
    @IntDef({NO_REPEAT, DAILY, WEEKLY, MONTHLY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RepeatTypes { }

}
