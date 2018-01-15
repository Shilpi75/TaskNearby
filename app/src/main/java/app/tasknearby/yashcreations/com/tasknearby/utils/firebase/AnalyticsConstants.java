package app.tasknearby.yashcreations.com.tasknearby.utils.firebase;

/**
 * @author shilpi
 */

public class AnalyticsConstants {

    /**
     * Constants for alarms.
     */
    public static final String ANALYTICS_ALARM_RING = "alarm_ring";
    public static final String ANALYTICS_ALARM_MARK_DONE = "alarm_mark_done";
    public static final String ANALYTICS_ALARM_SNOOZE = "alarm_snooze";
    public static final String ANALYTICS_ALARM_TO_NOTIFICATION = "alarm_to_notification";
    public static final String ANALYTICS_ALARM_SHOW_MAP = "alarm_show_map";

    /**
     * Constants for notification alarm.
     */
    public static final String ANALYTICS_NOTIFICATION_MARK_DONE = "notification_mark_done";
    public static final String ANALYTICS_NOTIFICATION_SNOOZE = "notificaiton_snooze";

    /**
     * Constants for navigation button from detail activity.
     */
    public static final String ANALYTICS_SHOW_MAP_FROM_DETAIL = "show_map_from_detail";

    /**
     * Constants for logging task creation events.
     */
    public static final String ANALYTICS_SAVE_NEW_TASK = "save_new_task";
    public static final String ANALYTICS_PARAM_START_TIME = "task_start_time";
    public static final String ANALYTICS_PARAM_END_TIME = "task_end_time";
    public static final String ANALYTICS_PARAM_IS_DEADLINE_SET = "is_deadline_set";
    public static final String ANALYTICS_PARAM_IS_NOTE_ADDED = "is_note_added";

    /**
     * Constants for app start.
     */
    public static final String ANALYTICS_APP_START = "app_start";
    public static final String ANALYTICS_APP_ENABLED = "app_enabled";
    public static final String ANALYTICS_APP_DISABLED = "app_disabled";
    public static final String ANALYTICS_PARAM_IS_POWER_SAVER_ON = "is_power_saver_on";
}
