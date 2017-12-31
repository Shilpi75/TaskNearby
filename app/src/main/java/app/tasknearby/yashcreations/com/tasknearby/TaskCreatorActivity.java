package app.tasknearby.yashcreations.com.tasknearby;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.tasknearby.yashcreations.com.tasknearby.database.DbConstants;
import app.tasknearby.yashcreations.com.tasknearby.models.Attachment;

public class TaskCreatorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = TaskCreatorActivity.class.getSimpleName();

    /**
     * Request code constants.
     */
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
     * For keeping track of selected location.
     */
    private boolean hasSelectedLocation = false;
    private LatLng mSelectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_creator);

        setActionBar();
        initializeViews();
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
                // TODO: start saved places activity for result here.
            case R.id.button_place_picker:
                // TODO: start place picker activity for result here.
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
        repeatTv.setOnClickListener(this);
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
                    v.setTag(new LocalTime(hourOfDay, minute));
                    // set selected Time on textView.
                    v.setText(getReadableTimeString(hourOfDay, minute));
                }, 12, 0, false); // time at which timepicker opens.
        timePickerDialog.show();
    }

    /**
     * Returns a formatted time string in 12-hour format.
     */
    private String getReadableTimeString(int hourOfDay, int minute) {
        String periodSuffix = "AM";
        if (hourOfDay > 12) {
            hourOfDay -= 12;
            periodSuffix = "PM";
        } else if (hourOfDay == 12) {
            periodSuffix = "PM";
        }
        return String.format(Locale.ENGLISH, "%02d:%02d %s", hourOfDay, minute, periodSuffix);
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
            v.setText(getReadableDateString(calendar.getTime()));
            Log.d(TAG, "Date selected: " + calendar.getTime().toString());
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener,
                calendar.get(Calendar.YEAR),            // current year.
                calendar.get(Calendar.MONTH),           // current month (0 indexed)
                calendar.get(Calendar.DAY_OF_MONTH));   // current day.
        datePickerDialog.show();
    }

    /**
     * Returns a formatted date String like "Wed, 26 Dec 2017".
     */
    private String getReadableDateString(Date date) {
        SimpleDateFormat sdfReadable = new SimpleDateFormat("EEE, d MMM yy", Locale.ENGLISH);
        return sdfReadable.format(date);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_SELECTION:
                // TODO: will be implemented.
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
        String reminderRange = reminderRangeInput.getText().toString();
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

        // Attachment will refer to the file and notes that are attached.
        // It will be null when note is not added.
        Attachment attachment = null;
        if (!TextUtils.isEmpty(noteInput.getText())) {
            attachment = new Attachment(noteInput.getText().toString());
        }
        // TODO: Save to database, all 3 tables.
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
        }
        return super.onOptionsItemSelected(item);
    }
}
