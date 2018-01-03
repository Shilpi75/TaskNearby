package app.tasknearby.yashcreations.com.tasknearby.services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.R;
import app.tasknearby.yashcreations.com.tasknearby.TaskRepository;
import app.tasknearby.yashcreations.com.tasknearby.database.DbConstants;
import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;

/**
 * Set location updates on based on detected activities.
 * TODO: Make it a foreground service.
 *
 * @author shilpi
 */

public class FusedLocationService extends Service {

    /**
     * Constants for creating location requests.
     */
    public static final long DEFAULT_LOCATION_UPDATE_INTERVAL = 5000;           // 5 seconds.
    public static final long FASTEST_LOCATION_UPDATE_INTERVAL = 3000;           // 3 seconds.

    /**
     * Constants for activity detection.
     */
    public static final long ACTIVITY_DETECTION_INTERVAL = 2000;                // 2 seconds.

    /**
     * Constants for update time intervals for different detected activities.
     */
    public static final long DRIVING_LOCATION_UPDATE_INTERVAL = 5000;           // 5 seconds.
    public static final long RUNNING_LOCATOIN_UPDATE_INTERVAL = 10000;          // 10 seconds.
    public static final long FAST_RUNNING_LOCATION_UPDATE_INTERVAL = 5000;      // 5 seconds.
    public static final long WALKING_LOCATION_UPDATE_INTERVAL = 15000;          // 15 seconds.
    public static final long UNKNOWN_LOCATION_UPDATE_INTERVAL = 10000;          // 10 seconds.

    public static final String TAG = FusedLocationService.class.getSimpleName();

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private ActivityDetectionReceiver mActivityDetectionReceiver;
    private TaskRepository mTaskRepository;


    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Creating LocationCallback, LocationRequest and LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest(DEFAULT_LOCATION_UPDATE_INTERVAL);

        // Set up activity detection receiver.
        mActivityDetectionReceiver = new ActivityDetectionReceiver();
        LocalBroadcastManager.getInstance(this).
                registerReceiver(mActivityDetectionReceiver,
                        new IntentFilter(ServiceConstants.ACTION_DETECTED_ACTIVITIES));
    }

    /**
     * Invoked when another component (such as an activity) requests that the service be started.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startLocationUpdates();
        startActivityDetection();

        return START_NOT_STICKY;
    }


    /**
     * Invoked the service is no longer used and is being destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        stopActivityDetection();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Creates location request to be used by fused location client.
     */
    public void createLocationRequest(long updateInterval) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(updateInterval);
        // TODO: Get the values from Settings.
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    public void createLocationCallback() {
        mLocationCallback = new LocationResultCallback(getApplicationContext());
    }

    /**
     * Checks for device settings and starts location updates.
     */
    public void startLocationUpdates() {
        // Permission check.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Missing permissions");
            return;
        }
        Task<Void> task = mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper());
        if (!task.isSuccessful()) {
            Log.e(TAG, "Location Update Request Failed");
        }

    }

    /**
     * Stops location updates.
     */
    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * Starts activity recognition.
     */
    public void startActivityDetection() {
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mActivityRecognitionClient.requestActivityUpdates(ACTIVITY_DETECTION_INTERVAL,
                getActivityDetectionPendingIntent());
    }

    /**
     * Returns a pending intent.
     *
     * @return
     */
    public PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivityDetectionService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Stops activity recognition.
     */
    public void stopActivityDetection() {
        mActivityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent());
    }


    /**
     * Restarts location updates with new update interval.
     *
     * @param updateInterval
     */
    public void restartLocationUpdates(long updateInterval) {
        if (mLocationRequest != null && mLocationRequest.getInterval() != updateInterval) {
            stopLocationUpdates();
            createLocationRequest(updateInterval);
            startLocationUpdates();
        }
    }

    /**
     * Receives the broadcasted intent by {@link ActivityDetectionService}.
     */
    public class ActivityDetectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Activity Detection Intent received.");

            ArrayList<DetectedActivity> detectedActivityList =
                    intent.getParcelableArrayListExtra(ServiceConstants.EXTRA_DETECTED_ACTIVITIES);
            adjustLocationUpdates(detectedActivityList);
        }

        /**
         * Adjusts location updates based on the detected activity of the device.
         *
         * @param detectedActivityList
         */
        public void adjustLocationUpdates(ArrayList<DetectedActivity> detectedActivityList) {

            for (DetectedActivity detectedActivity : detectedActivityList) {
                int confidence = detectedActivity.getConfidence();
                switch (detectedActivity.getType()) {
                    case DetectedActivity.STILL:
                        if (confidence > 50) {
                            stopLocationUpdates();
                        }
                        break;

                    case DetectedActivity.IN_VEHICLE:
                        if (confidence > 50) {
                            restartLocationUpdates(DRIVING_LOCATION_UPDATE_INTERVAL);
                        }
                        break;

                    case DetectedActivity.RUNNING:
                    case DetectedActivity.ON_BICYCLE:
                        if (confidence > 60) {
                            restartLocationUpdates(FAST_RUNNING_LOCATION_UPDATE_INTERVAL);
                        } else if (confidence > 50) {
                            restartLocationUpdates(RUNNING_LOCATOIN_UPDATE_INTERVAL);
                        }
                        break;

                    case DetectedActivity.ON_FOOT:
                    case DetectedActivity.WALKING:
                        if (confidence > 50) {
                            restartLocationUpdates(WALKING_LOCATION_UPDATE_INTERVAL);
                        }
                        break;

                    default:
                        restartLocationUpdates(UNKNOWN_LOCATION_UPDATE_INTERVAL);
                }
            }
        }
    }
}
