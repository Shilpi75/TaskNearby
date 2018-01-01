package app.tasknearby.yashcreations.com.tasknearby.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import org.joda.time.LocalTime;

import java.util.Date;

import app.tasknearby.yashcreations.com.tasknearby.database.converters.DateConverter;
import app.tasknearby.yashcreations.com.tasknearby.database.converters.TimeConverter;

/**
 * @author shilpi
 */

@Entity(tableName = "tasks",
        foreignKeys = {@ForeignKey(entity = Location.class, parentColumns = "id", childColumns = "location_id"),
                @ForeignKey(entity = Attachment.class, parentColumns = "id", childColumns = "attachment_id")})
@TypeConverters({DateConverter.class, TimeConverter.class})
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "task_name")
    private String taskName;

    @ColumnInfo(name = "location_id")
    private long locationId;

    @ColumnInfo(name = "image_uri")
    private String imageUri;

    @ColumnInfo(name = "is_done")
    private int isDone;

    @ColumnInfo(name = "is_alarm_set")
    private int isAlarmSet;

    @ColumnInfo(name = "reminder_range")
    private int reminderRange;

    @ColumnInfo(name = "attachment_id")
    private long attachmentId;

    @ColumnInfo(name = "start_time")
    private LocalTime startTime;

    @ColumnInfo(name = "end_time")
    private LocalTime endTime;

    @ColumnInfo(name = "start_date")
    private Date startDate;

    @ColumnInfo(name = "end_date")
    private Date endDate;

    @ColumnInfo(name = "next_start_date")
    private Date nextStartDate;

    /**
     * Repeat type as NO REPEAT(0), REPEAT_DAILY(1), REPEAT_WEEKLY(2), REPEAT_MONTHLY(3).
     */
    @ColumnInfo(name = "repeat_type")
    private int repeatType;

    /**
     * Movement Type as BOTH ENTER AND EXIT(0), ENTER(1), EXIT(2).
     */
    @ColumnInfo(name = "movement_type")
    private int movementType;

    /**
     * Activity Type as ANYTHING(0), WALKING(1), DRIVING(2).
     */
    @ColumnInfo(name = "activity_type")
    private int activityType;

    @ColumnInfo(name = "last_distance")
    private float lastDistance;

    @ColumnInfo(name = "last_triggered")
    private Date lastTriggered;

    @ColumnInfo(name = "snoozed_at")
    private Long snoozedAt;

    @ColumnInfo(name = "date_added")
    private Date dateAdded;


    public Task() {
    }

    @Ignore
    public Task(String taskName, long locationId, String imageUri, int isDone,
                int is_alarm_set, int reminderRange, long attachmentId, LocalTime startTime,
                LocalTime endTime, Date startDate, Date endDate, Date nextStartDate,
                int repeatType, int movementType, int activityType, float lastDistance,
                Date lastTriggered, Long snoozedAt, Date dateAdded) {
        this.taskName = taskName;
        this.locationId = locationId;
        this.imageUri = imageUri;
        this.isDone = isDone;
        this.isAlarmSet = is_alarm_set;
        this.reminderRange = reminderRange;
        this.attachmentId = attachmentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextStartDate = nextStartDate;
        this.repeatType = repeatType;
        this.movementType = movementType;
        this.activityType = activityType;
        this.lastDistance = lastDistance;
        this.lastTriggered = lastTriggered;
        this.snoozedAt = snoozedAt;
        this.dateAdded = dateAdded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }

    public int getIsAlarmSet() {
        return isAlarmSet;
    }

    public void setIsAlarmSet(int isAlarmSet) {
        this.isAlarmSet = isAlarmSet;
    }

    public int getReminderRange() {
        return reminderRange;
    }

    public void setReminderRange(int reminderRange) {
        this.reminderRange = reminderRange;
    }

    public long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getNextStartDate() {
        return nextStartDate;
    }

    public void setNextStartDate(Date nextStartDate) {
        this.nextStartDate = nextStartDate;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public int getMovementType() {
        return movementType;
    }

    public void setMovementType(int movementType) {
        this.movementType = movementType;
    }

    public int getActivityType() {
        return activityType;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public float getLastDistance() {
        return lastDistance;
    }

    public void setLastDistance(float lastDistance) {
        this.lastDistance = lastDistance;
    }

    public Date getLastTriggered() {
        return lastTriggered;
    }

    public void setLastTriggered(Date lastTriggered) {
        this.lastTriggered = lastTriggered;
    }

    public Long getSnoozedAt() {
        return snoozedAt;
    }

    public void setSnoozedAt(Long snoozedAt) {
        this.snoozedAt = snoozedAt;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
