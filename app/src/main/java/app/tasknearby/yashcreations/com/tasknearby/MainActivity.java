package app.tasknearby.yashcreations.com.tasknearby;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import app.tasknearby.yashcreations.com.tasknearby.service.FusedLocationService;
import app.tasknearby.yashcreations.com.tasknearby.service.ActivityDetectionService;

/**
 * App's working: It relies on FusedLocation provided by Google play services to obtain location.
 * In FusedLocation API, We can adjust the interval in which we want the location. We should keep
 * the interval as minimum as possible so, the app intelligently uses Activity Recognition API to
 * recognize what the user is doing. If we find that the mobile is at rest, we set the interval
 * much higher (1 min) and when he's driving (3-5 seconds) and otherwise in between. So,
 * basically the app obtains the data from these 2 APIs. Rest all are the algorithms implemented
 * to use this data.
 * <p>
 * Activities :-
 *
 * @see MainActivity - opens first as of now and shows the Add Task FAB and TasksFragment.
 * @see TasksFragment - gets the cursor from database and displays the added tasks on display.
 * @see NewTaskActivity - provides a UI to add a task, also, editing a task also comes here only,
 * data
 * * is already filled in that case.
 * @see SavedLocationListActivity - displays the list of saved places and option to delete them.
 * @see com.google.android.gms.location.places.ui.PlacePicker is provided by Google Play services.
 * @see TaskDetailActivity - displays the details of the task, click to edit, directions etc.
 * @see AlarmActivity - shows when alarm rings.
 * <p>
 * All these were just data display activities, now the services which actually run the app.
 * @see FusedLocationService sets a listener to the FusedLocation API by using the data obtained
 * from ActivittRecognition API to adjust the update interval. After setting the listener, on
 * receiving location updates, it checks the active tasks in database and finds which are
 * eligible for alarms and if anyone is found eligible, it starts the AlarmActivity.
 * @see ActivityDetectionService it is used as an input for results from Google play services.
 * @see Utility contains a collection of methods used in app, which, ideally should not be there.
 * @see app.tasknearby.yashcreations.com.tasknearby.database.TasksProvider - provides an
 * abstraction to the database, just insert, update delete query functions. Not required if you
 * don't have a widget and are not sharing your data with other apps.
 * <p>
 * <p>
 * Go in this order :
 * <p>
 * Note: Just skim over the database. Don't read what components do, learn Room, we'll be using
 * that.
 * a) Database - 1. TasksContract 2. TasksDbHelper 3. TasksProvider
 * b) ActivityDetectionService
 * c) FusedLocationService
 * d) You've got 90% of the app. rest all will be written from scratch completely.
 * remaining are activities.
 * <p>
 * <p>
 * <p>
 * Running
 * </p>
 * I've setup checkstyle and findbugs check. They will run only when you do
 * ./gradlew build  or  ./gradlew check
 * If you want to disable them, which you should not, then set ignoreViolations to true in build
 * .gradle.
 * You might install Findbugs and Checkstyle plugins for android studio, but not necessary as
 * of now. Do that later.
 * <p>
 * Note - when you'll build this app locally, debug version -> the place selector won't work.
 * You'll have to run the following command to generate your SHA1 fingerprint of debug.keystore
 * keytool -list -v -keystore ~/.android/debug.keystore
 * get the fingerprint to me, I'll add it to allowed list in Google Developers Console.
 * </p>
 * Continuous Integration is setup for this project, as soon as you push your changes to any
 * branch, it'll trigger the build on travis ci. I'll configure it to run only when master or
 * develop is pushed.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Utility utility;
    private static boolean isServiceRunning = false;
    private SwitchCompat appSwitch;
    private FirebaseAnalytics mAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TasksFragment(), TAG)
                    .commit();
        }
        mAnalytics = FirebaseAnalytics.getInstance(this);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setElevation(0);
        }
        TextView mTitleView = (TextView) toolbar.findViewById(R.id.toolbarTV);
        mTitleView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf"));

        utility = new Utility();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            startApp();
        else {
            checkPermissions();
        }

        //TODO: Ad code
       /* String android_id = Settings.Secure.getString(this.getContentResolver(), Settings
       .Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();
        mAdView.landroid oadAd(adRequest);
        boolean isTestDevice = adRequest.isTestDevice(this);
        Log.e(TAG, "is Admob Test Device ? "+deviceId+" "+isTestDevice);
*/
    }

    //    http://www.javacreed.com/why-should-we-use-dependency-injection/
    private void checkPermissions() {
        boolean mFinePermission = ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean mCoarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (mFinePermission && mCoarsePermission)
            startApp();
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    checkPermissions();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string
                            .no_permissions_granted), Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void startApp() {
        appSwitch = (SwitchCompat) this.findViewById(R.id.app_switch);
        LocationManager locationManager = (LocationManager) getSystemService(Context
                .LOCATION_SERVICE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String accuracyString = prefs.getString(getString(R.string.pref_accuracy_key), getString
                (R.string.pref_accuracy_default));
        String appStatus = prefs.getString(getString(R.string.pref_status_key), getString(R
                .string.pref_status_default));

        Bundle bundle = new Bundle();
        bundle.putBoolean("app_started", true);
        bundle.putBoolean("gps_status", locationManager.isProviderEnabled(locationManager
                .GPS_PROVIDER));
        bundle.putString("accuracy_settings", accuracyString);
        bundle.putString("app_status", appStatus);
        mAnalytics.logEvent(Constants.ANALYTICS_KEY_APP_OPENED, bundle);

        if (/*accuracyString.equals(getString(R.string.pref_accuracy_default)) &&
        */!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
            showGpsOffDialog(this);

        if (isAppEnabled(this) && utility.checkPlayServices(this)) {
            startServ();
            appSwitch.setChecked(true);
        } else
            appSwitch.setChecked(false);

        appSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                        (MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                if (appSwitch.isChecked()) {
                    if (!isServiceRunning)              //If service is not running then start it!
                        startServ();
                    editor.putString(MainActivity.this.getString(R.string.pref_status_key),
                            "enabled");
                } else {
                    if (isServiceRunning)
                        stopServ();
                    editor.putString(MainActivity.this.getString(R.string.pref_status_key),
                            "disabled");
                }
                editor.apply();
            }
        });

    }

    private void showGpsOffDialog(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(getString(R.string.gps_off_dialog_title))
                .setIcon(R.drawable.ic_location_off_teal_500_24dp)
                .setMessage(getString(R.string.gps_off))
                .setPositiveButton(getString(R.string.turn_on_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings
                                        .ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(intent);
                            }
                        });
        alertDialog.show();
    }

    public void startServ() {
        startService(new Intent(this, FusedLocationService.class));
        isServiceRunning = true;
    }

    void stopServ() {
        stopService(new Intent(this, FusedLocationService.class));
        isServiceRunning = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Snackbar.make(findViewById(android.R.id.content), "Task Added!", Snackbar
                    .LENGTH_LONG).show();
            TextView tv = (TextView) this.findViewById(R.id.textView);
            tv.setVisibility(View.INVISIBLE);
            mAnalytics.logEvent(Constants.ANALYTICS_KEY_TASK_ADDED, new Bundle());
        }
    }

    public static boolean isAppEnabled(Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String m = prefs.getString(mContext.getString(R.string.pref_status_key),
                mContext.getString(R.string.pref_status_default));
        return m.equals("enabled");
    }

    public static class OnBootStarter extends BroadcastReceiver {
        public OnBootStarter() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: BootCompletedReceived");
            boolean mFinePermission = ContextCompat.checkSelfPermission(context, Manifest
                    .permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (mFinePermission && isAppEnabled(context)) {
                Log.e(TAG, "onReceive: Starting service now.");
                context.startService(new Intent(context, FusedLocationService.class));
                isServiceRunning = true;
            }
        }
    }


}
