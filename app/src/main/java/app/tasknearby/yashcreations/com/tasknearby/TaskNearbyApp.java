package app.tasknearby.yashcreations.com.tasknearby;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;

import static app.tasknearby.yashcreations.com.tasknearby.utils.firebase.AnalyticsConstants.APPLICATION_CLASS_INIT;
import static app.tasknearby.yashcreations.com.tasknearby.utils.firebase.AnalyticsConstants.PARAM_DEVICE_DEFAULT_LANGUAGE;

public class TaskNearbyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        // Publishing a metric for the default device locale here. This will help us in efficiently
        // targeting which new locales to localize Task Nearby to.
        final String deviceLanguageCode = Locale.getDefault().getLanguage();
        final Bundle metricsBundle = new Bundle();
        metricsBundle.putString(PARAM_DEVICE_DEFAULT_LANGUAGE, deviceLanguageCode);
        firebaseAnalytics.logEvent(APPLICATION_CLASS_INIT, metricsBundle);
    }
}
