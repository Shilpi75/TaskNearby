package app.tasknearby.yashcreations.com.tasknearby;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Calendar;
import java.util.Date;

import app.tasknearby.yashcreations.com.tasknearby.database.DbConstants;
import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;
import app.tasknearby.yashcreations.com.tasknearby.utils.AppUtils;
import app.tasknearby.yashcreations.com.tasknearby.utils.DistanceUtils;

/**
 * Creates a new task and also responsible for editing an old one. For editing, we need to use
 * the getEditModeIntent() method to get the starting intent.
 *
 * @author vermayash8
 */
public class TaskCreatorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = TaskCreatorActivity.class.getSimpleName();

    /**
     * Since this activity serves both edit and add task operations, when this extra is set in
     * the calling intent, it will be started in edit mode.
     */
    private static final String EXTRA_EDIT_MODE_TASK_ID = "editTaskIdTaskCreatorActivity";

    /**
     * Request code constants.
     */
    private static final int REQUEST_CODE_PLACE_PICKER = 0;
    private static final int REQUEST_CODE_LOCATION_SELECTION = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 2;
    private static final int REQUEST_CODE_CAMERA_IMAGE = 3;
    private static final int REQUEST_CODE_GALLERY_IMAGE_PICKER = 4;

    private EditText taskNameInput;
    private EditText locationNameInput;
    private EditText reminderRangeInput;
    private EditText noteInput;
    private TextView startTimeTv, endTimeTv;
    private TextView startDateTv, endDateTv;
    private TextView repeatTv;
    private ImageView coverImageView;
    private Switch alarmSwitch;
    private Switch anytimeSwitch;

    /**
     * Tells if the task present is being edited or a new one is being created.
     */
    private TaskModel taskBeingEdited = null;

    /**
     * For keeping track of selected location.
     */
    private boolean hasSelectedLocation = false;
    private LocationModel mSelectedLocation;

    private TaskRepository mTaskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_creator);
        setActionBar();
        // Find views and set click listeners.
        initializeViews();
        mTaskRepository = new TaskRepository(getApplicationContext());
        // check if activity has been started for editing a task.
        if (getIntent().hasExtra(EXTRA_EDIT_MODE_TASK_ID)) {
            long taskId = getIntent().getLongExtra(EXTRA_EDIT_MODE_TASK_ID, -1);
            taskBeingEdited = mTaskRepository.getTaskWithId(taskId);
            fillDataForEditing(taskBeingEdited);
            getSupportActionBar().setTitle(getString(R.string.title_edit_task_reminder));
        }
    }

    /**
     * This will be used to get the intent to start this activity when we need to edit the task.
     *
     * @param context context of the calling activity.
     * @param taskId  taskId of the task to be edited.
     * @return intent that can be used in startActivity.
     */
    public static Intent getEditModeIntent(Context context, long taskId) {
        Intent intent = new Intent(context, TaskCreatorActivity.class);
        intent.putExtra(EXTRA_EDIT_MODE_TASK_ID, taskId);
        return intent;
    }

    /**
     * Sets the toolbar as actionBar and also sets the up button.
     */
    private void setActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_image:
                addTaskImage();
                break;
            case R.id.button_saved_places:
                Intent savedPlacesIntent = new Intent(this, SavedPlacesActivity.class);
                startActivityForResult(savedPlacesIntent, REQUEST_CODE_LOCATION_SELECTION);
                break;
            case R.id.button_place_picker:
                onPlacePickerRequested();
                break;
            case R.id.text_start_time:
            case R.id.text_end_time:
                timeSelectionTriggered((TextView) v);
                break;
            case R.id.text_start_date:
            case R.id.text_end_date:
                dateSelectionTriggered((TextView) v);
                break;
            case R.id.text_repeat_selection:
                showRepeatSelection();
                break;
            default:
                break;
        }
    }

    /**
     * Finds views by id and sets OnClickListener to them.
     */
    private void initializeViews() {
        //TODO: Use ButterKnife and remove this boilerplate code.
        // These don't have an OnClickListener.
        taskNameInput = findViewById(R.id.edit_text_task_name);
        locationNameInput = findViewById(R.id.edit_text_location_name);
        reminderRangeInput = findViewById(R.id.edit_text_reminder_range);
        noteInput = findViewById(R.id.edit_text_note);
        coverImageView = findViewById(R.id.image_task_cover);
        alarmSwitch = findViewById(R.id.switch_alarm);

        // image choosing FAB and location buttons.
        findViewById(R.id.fab_image).setOnClickListener(this);
        findViewById(R.id.button_saved_places).setOnClickListener(this);
        findViewById(R.id.button_place_picker).setOnClickListener(this);

        // switch to show/hide the time selection layout.
        anytimeSwitch = findViewById(R.id.switch_time_interval);
        anytimeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            findViewById(R.id.ll_time_adjust).setVisibility((isChecked) ? View.GONE : View.VISIBLE);
        });

        // time TextViews.
        startTimeTv = findViewById(R.id.text_start_time);
        endTimeTv = findViewById(R.id.text_end_time);
        startTimeTv.setOnClickListener(this);
        endTimeTv.setOnClickListener(this);
        // Set the initial times as tag on time textViews.
        startTimeTv.setTag(new LocalTime(0, 0));
        endTimeTv.setTag(new LocalTime(23, 59));

        // date textViews.
        startDateTv = findViewById(R.id.text_start_date);
        endDateTv = findViewById(R.id.text_end_date);
        startDateTv.setOnClickListener(this);
        endDateTv.setOnClickListener(this);
        // Set initial dates as text on date textViews.
        startDateTv.setTag(new Date());
        endDateTv.setTag(null);

        // textView to repeat reminders.
        repeatTv = findViewById(R.id.text_repeat_selection);
        repeatTv.setTag(DbConstants.NO_REPEAT);
        repeatTv.setOnClickListener(this);
    }

    /**
     * When we've a task that is being edited, we've to fill it's attributes into the input fields.
     *
     * @param task The task that is being edited.
     */
    private void fillDataForEditing(final TaskModel task) {
        taskNameInput.setText(task.getTaskName());
        // Set location
        mSelectedLocation = mTaskRepository.getLocationById(task.getLocationId());
        locationNameInput.setText(mSelectedLocation.getPlaceName());
        hasSelectedLocation = true;
        // Set reminder range
        reminderRangeInput.setText(String.valueOf(task.getReminderRange()));
        // Set note
        noteInput.setText(task.getNote());
        // Setup time.
        boolean anytime = task.getStartTime().equals(new LocalTime(0, 0))
                && task.getEndTime().equals(new LocalTime(23, 59));
        anytimeSwitch.setChecked(anytime);
        startTimeTv.setText(AppUtils.getReadableTime(task.getStartTime()));
        endTimeTv.setText(AppUtils.getReadableTime(task.getEndTime()));
        startTimeTv.setTag(task.getStartTime());
        endTimeTv.setTag(task.getEndTime());

        // Set date.
        startDateTv.setText(AppUtils.getReadableLocalDate(this, task.getStartDate()));
        endDateTv.setText(AppUtils.getReadableLocalDate(this, task.getEndDate()));
        startDateTv.setTag(task.getStartDate());
        endDateTv.setTag(task.getEndDate());

        // Repeat options.
        String[] repeatOptions = getResources().getStringArray(R.array.creator_repeat_options);
        repeatTv.setTag(task.getRepeatType());
        repeatTv.setText(repeatOptions[task.getRepeatType()]);

        // Alarm switch
        alarmSwitch.setChecked(task.getIsAlarmSet() != 0);
        // Cover image
        if (task.getImageUri() != null) {
            coverImageView.setImageURI(Uri.parse(task.getImageUri()));
        }
    }

    /**
     * Triggered when FAB is clicked to add image and when permissions are granted. This also
     * checks and requests if required permissions are not available.
     */
    private void addTaskImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            // Permission is available.
            // TODO: Show a dialog here to allow the user to use camera or gallery.
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_CODE_GALLERY_IMAGE_PICKER);
        }
    }

    /**
     * Called when user clicks on time display.
     */
    private void timeSelectionTriggered(TextView v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    Log.d(TAG, "Time selected, " + hourOfDay + ":" + minute);
                    // storing the time object in the textView itself.
                    LocalTime localTime = new LocalTime(hourOfDay, minute);
                    v.setTag(localTime);
                    // set selected Time on textView.
                    v.setText(AppUtils.getReadableTime(localTime));
                }, 12, 0, false); // time at which timepicker opens.
        timePickerDialog.show();
    }

    /**
     * Called when user clicks on Date display.
     */
    private void dateSelectionTriggered(TextView v) {
        Calendar calendar = Calendar.getInstance();
        // what to do when date is set.
        DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month,
                                                                dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            v.setTag(calendar.getTime());
            v.setText(AppUtils.getReadableDate(this, calendar.getTime()));
            Log.d(TAG, "Date selected: " + calendar.getTime().toString());
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener,
                calendar.get(Calendar.YEAR),            // current year.
                calendar.get(Calendar.MONTH),           // current month (0 indexed)
                calendar.get(Calendar.DAY_OF_MONTH));   // current day.
        datePickerDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_STORAGE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addTaskImage();
                } else {
                    Toast.makeText(this, R.string.creator_error_image_permission,
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * Triggered when the user clicks on the Pick Place button.
     */
    private void onPlacePickerRequested() {
        PlacePicker.IntentBuilder placePickerIntent = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(placePickerIntent.build(this),
                    REQUEST_CODE_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle this repairable exception.
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PLACE_PICKER:
                if (resultCode == RESULT_OK) {
                    onPlacePickerSuccess(data);
                }
                break;
            case REQUEST_CODE_LOCATION_SELECTION:
                if (resultCode == RESULT_OK) {
                    onSavedPlacesSuccess(data);
                } else if (resultCode == SavedPlacesActivity.RESULT_USE_PLACE_PICKER) {
                    // We're showing a button in saved places activity when no places are being
                    // shown to allow the users to select pick a place option from there only.
                    // Didn't want to implement the same place picker calling functionality there,
                    // so using this way.
                    onPlacePickerRequested();
                }
                break;
            case REQUEST_CODE_CAMERA_IMAGE:
            case REQUEST_CODE_GALLERY_IMAGE_PICKER:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    coverImageView.setImageURI(selectedImageUri);
                    // for retrieval later on.
                    coverImageView.setTag(selectedImageUri);
                    Log.d(TAG, "Image selected, Uri: " + selectedImageUri);
                }
                break;
            default:
                Log.w(TAG, "Unknown request code in onActivityResult.");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Initializes the location with place picker returned data. Also sets that to the UI.
     */
    private void onPlacePickerSuccess(Intent data) {
        Place place = PlacePicker.getPlace(this, data);
        // Create a new location object.
        mSelectedLocation = new LocationModel(place.getName().toString(),
                place.getLatLng().latitude,
                place.getLatLng().longitude,
                0, 0, new LocalDate());
        hasSelectedLocation = true;
        onLocationSelected();
    }

    /**
     * Gets the result from saved places selection activity and sets the location.
     */
    private void onSavedPlacesSuccess(Intent data) {
        if (data == null || !data.hasExtra(SavedPlacesActivity.EXTRA_LOCATION_ID)) {
            Log.w(TAG, "No location id was returned by SavedPlacesActivity");
            return;
        }
        long locationId = data.getLongExtra(SavedPlacesActivity.EXTRA_LOCATION_ID, -1);
        mSelectedLocation = mTaskRepository.getLocationById(locationId);
        hasSelectedLocation = true;
        onLocationSelected();
    }

    /**
     * Sets the selected location's name to the input textView.
     */
    private void onLocationSelected() {
        locationNameInput.setText(mSelectedLocation.getPlaceName());
        findViewById(R.id.text_input_location_name).setVisibility(View.VISIBLE);
    }

    /**
     * Validates the input entered by the user.
     */
    private boolean isInputValid() {
        String errorMsg;
        if (TextUtils.isEmpty(taskNameInput.getText())) {
            errorMsg = getString(R.string.creator_error_empty_taskname);
        } else if (TextUtils.isEmpty(locationNameInput.getText()) || !hasSelectedLocation) {
            errorMsg = getString(R.string.creator_error_empty_location);
        } else if (TextUtils.isEmpty(reminderRangeInput.getText())) {
            errorMsg = getString(R.string.creator_error_empty_range);
        } else {
            return true;
        }
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        return false;
    }


    /**
     * Validates the input and saves the result to the database.
     */
    private void saveTask() {
        if (!isInputValid()) {
            return;
        }
        String taskName = taskNameInput.getText().toString();
        String locationName = locationNameInput.getText().toString();
        int enteredReminderRange = Integer.parseInt(reminderRangeInput.getText().toString());
        int reminderRange = (int) DistanceUtils.getDistanceToSave(this, enteredReminderRange);
        boolean isAlarmEnabled = alarmSwitch.isChecked();
        String imageUri = null;
        Uri selectedImageUri = (Uri) coverImageView.getTag();
        if (selectedImageUri != null) {
            imageUri = selectedImageUri.toString();
        }
        // There can be a case when user selects a time and then turns on the anytime switch.
        // So, we need to check the anytime switch first.
        LocalTime startTime, endTime;
        if (anytimeSwitch.isChecked()) {
            // Alarm can ring anytime. Therefore, we can set the times internally to be from
            // 00:00 to 23:59
            startTime = new LocalTime(0, 0);
            endTime = new LocalTime(23, 59);
        } else {
            // See what times are set on the textViews.
            startTime = (LocalTime) startTimeTv.getTag();
            endTime = (LocalTime) endTimeTv.getTag();
        }

        Date startDate = (Date) startDateTv.getTag();
        // end date will be stored as null only.
        Date endDate = (Date) endDateTv.getTag();

        // repeat mode.
        int repeatType = (int) repeatTv.getTag();

        String note = noteInput.getText().toString();
        if (TextUtils.isEmpty(note)) {
            note = null;
        }

        mSelectedLocation.setPlaceName(locationName);
        // TODO:
        // After location picker has been implemented, check if this works in both cases?
        // When picking a location from the saved places, we won't create a new location
        // but instead update the old one. So, if we insert a new location with the same
        // id, will it cause a conflict or update the old one.
        long locationId = mTaskRepository.saveLocation(mSelectedLocation);

        TaskModel task = new TaskModel.Builder(this, taskName, locationId)
                .setReminderRange(reminderRange)
                .setIsAlarmSet(isAlarmEnabled ? 1 : 0)
                .setImageUri(imageUri)
                .setNote(note)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setStartDate(LocalDate.fromDateFields(startDate))
                .setEndDate(LocalDate.fromDateFields(endDate))
                .setRepeatType(repeatType)
                .build();

        if (taskBeingEdited == null) {
            // add new task.
            mTaskRepository.saveTask(task);
        } else {
            // update task.
            task.setId(taskBeingEdited.getId());
            mTaskRepository.updateTask(task);
        }
    }

    /**
     * Shows a list containing categories for repeat.
     */
    private void showRepeatSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.creator_repeat_dialog_title);
        String[] repeatOptions = getResources().getStringArray(R.array.creator_repeat_options);
        int checkedItem = 0;
        builder.setSingleChoiceItems(repeatOptions, checkedItem, (dialog, which) -> {
            repeatTv.setText(repeatOptions[which]);
            switch (which) {
                case 0:
                    repeatTv.setTag(DbConstants.NO_REPEAT);
                case 1:
                    repeatTv.setTag(DbConstants.REPEAT_DAILY);
                    break;
                case 2:
                    repeatTv.setTag(DbConstants.REPEAT_WEEKLY);
                    break;
                case 3:
                    repeatTv.setTag(DbConstants.REPEAT_MONTHLY);
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_creator, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveTask();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
