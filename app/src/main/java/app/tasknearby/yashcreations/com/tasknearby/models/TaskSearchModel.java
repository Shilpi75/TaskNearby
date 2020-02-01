package app.tasknearby.yashcreations.com.tasknearby.models;

/**
 * Model containing task's trimmed task name and location name.
 */
public class TaskSearchModel {

    private String taskNameTrimmed;

    private String locationNameTrimmed;

    public TaskSearchModel() {
    }

    public TaskSearchModel(String taskNameTrimmed, String locationNameTrimmed) {
        this.taskNameTrimmed = taskNameTrimmed;
        this.locationNameTrimmed = locationNameTrimmed;
    }

    public String getTaskNameTrimmed() {
        return taskNameTrimmed;
    }

    public void setTaskNameTrimmed(String taskNameTrimmed) {
        this.taskNameTrimmed = taskNameTrimmed;
    }

    public String getLocationNameTrimmed() {
        return locationNameTrimmed;
    }

    public void setLocationNameTrimmed(String locationNameTrimmed) {
        this.locationNameTrimmed = locationNameTrimmed;
    }
}
