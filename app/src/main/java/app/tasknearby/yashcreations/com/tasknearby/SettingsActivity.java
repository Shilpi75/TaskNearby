package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import app.tasknearby.yashcreations.com.tasknearby.utils.AppUtils;
import app.tasknearby.yashcreations.com.tasknearby.utils.firebase.AnalyticsConstants;

import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_alarm_tone_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_distance_range_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_power_saver_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_snooze_time_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_unit_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_vibrate_key;

/**
 * Manages the settings/preferences.
 *
 * @author shilpi
 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setActionBar();

        getFragmentManager().beginTransaction().add(R.id.contentFrame, new SettingsFragment())
                .commit();
    }

    /**
     * Sets the toolbar as actionBar and also sets the up button.
     */
    public void setActionBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * It initializes the settings preferences and attaches preference change listener with each.
     */
    public static class SettingsFragment extends PreferenceFragment implements Preference
            .OnPreferenceChangeListener {

        private ListPreference mUnitPreference, mSnoozePreference, mVibratePreference;
        private RingtonePreference mAlarmTonePreference;
        private EditTextPreference mDistancePreference;
        private SwitchPreference mVoiceAlarmPreference, mPowerSaverPreference;
        private FirebaseAnalytics mFirebaseAnalytics;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            addPreferencesFromResource(R.xml.preferences);
            initializeViews();
        }

        /**
         * Attaches a listener so the summary is always updated with the preference value.
         * Also fires the listener once, to initialize the summary (so it shows up before the value
         * is changed.)
         */
        public void bindPreferenceSummaryToValue(Preference preference) {

            // Attach listener to preference.
            preference.setOnPreferenceChangeListener(this);

            // Initial firing of listener to update summary values.
            if (preference instanceof SwitchPreference) {
                onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences
                        (preference.getContext()).getBoolean(preference.getKey(), false));

            } else if (preference instanceof RingtonePreference) {
                onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences
                        (preference.getContext()).getString(preference.getKey(), Settings.System
                        .DEFAULT_ALARM_ALERT_URI.getPath()));

            } else {
                onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences
                        (preference.getContext()).getString(preference.getKey(), ""));
            }
        }

        /**
         * Listens to the changes in preference value.
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(o.toString());
                if (index >= 0) {
                    preference.setSummary(listPreference.getEntries()[index]);
                } else {
                    preference.setSummary(null);
                }
            } else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals(getString(pref_distance_range_key))) {
                    preference.setSummary(o.toString() + " units");
                } else {
                    preference.setSummary(o.toString());
                }

            } else if (preference instanceof RingtonePreference) {
                Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse(o
                        .toString()));
                String summary = ringtone.getTitle(getActivity());
                preference.setSummary(summary);

            } else if (preference instanceof SwitchPreference) {
                if (preference.getKey().equals(getString(pref_power_saver_key))) {
                    // Stop the service.
                    AppUtils.stopService(getActivity());
                    // Check if app is enabled.
                    SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences
                            (getActivity());
                    String appStatus = defaultPref.getString(getString(R.string.pref_status_key),
                            getString(R.string.pref_status_default));
                    if (appStatus.equals(getString(R.string.pref_status_enabled))) {
                        // Start the service again.
                        AppUtils.startService(getActivity());
                    }
                }

            } else {
                preference.setSummary(o.toString());
            }

            return true;
        }

        /**
         * Finds views by id and binds their preference summaries to their values.
         */
        public void initializeViews() {
            mUnitPreference = (ListPreference) getPreferenceManager().findPreference(getString
                    (pref_unit_key));
//            mDistancePreference = (EditTextPreference) getPreferenceManager().findPreference
//                    (getString(pref_distance_range_key));
            mAlarmTonePreference = (RingtonePreference) getPreferenceManager().findPreference
                    (getString(pref_alarm_tone_key));
            mSnoozePreference = (ListPreference) getPreferenceManager().findPreference(getString
                    (pref_snooze_time_key));
            mVibratePreference = (ListPreference) getPreferenceManager().findPreference(getString
                    (pref_vibrate_key));
            mPowerSaverPreference = (SwitchPreference) getPreferenceManager().findPreference
                    (getString(pref_power_saver_key));
            mVoiceAlarmPreference = (SwitchPreference) getPreferenceManager().findPreference
                    (getString(R.string.pref_voice_alarm_key));


            bindPreferenceSummaryToValue(mUnitPreference);
//            bindPreferenceSummaryToValue(mDistancePreference);
            bindPreferenceSummaryToValue(mAlarmTonePreference);
            bindPreferenceSummaryToValue(mSnoozePreference);
            bindPreferenceSummaryToValue(mVibratePreference);
            bindPreferenceSummaryToValue(mVoiceAlarmPreference);
            bindPreferenceSummaryToValue(mPowerSaverPreference);

            // Makes sure that voice alarms can be adjusted only in premium version.
            mVoiceAlarmPreference.setOnPreferenceClickListener(preference -> {
                if (!AppUtils.isPremiumUser(getActivity())) {
                    UpgradeActivity.show(getActivity());
                    mVoiceAlarmPreference.setChecked(false);
                }
                return true;
            });

            mPowerSaverPreference.setOnPreferenceClickListener(preference -> {
                SharedPreferences pref = preference.getSharedPreferences();
                boolean isPowerSaver = pref.getBoolean(getString(R.string.pref_power_saver_key),
                        false);
                if (isPowerSaver) {
                    mFirebaseAnalytics.logEvent(AnalyticsConstants.POWER_SAVER_TURN_ON, new
                            Bundle());
                } else {
                    mFirebaseAnalytics.logEvent(AnalyticsConstants.POWER_SAVER_TURN_OFF, new
                            Bundle());
                }
                return true;
            });
        }
    }
}

class CustomListPreference extends ListPreference {

    public CustomListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView(final ViewGroup parent) {
        final View view = super.onCreateView(parent);
        view.setOnClickListener(v -> {
            if (!AppUtils.isPremiumUser(getContext())) {
                UpgradeActivity.show(getContext());
            } else {
                CustomListPreference.super.onClick();
            }
        });
        return view;
    }
}



