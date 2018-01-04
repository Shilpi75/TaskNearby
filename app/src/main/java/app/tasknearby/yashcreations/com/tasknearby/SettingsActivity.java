package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Intent;
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

import app.tasknearby.yashcreations.com.tasknearby.services.FusedLocationService;

import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_alarm_tone_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_distance_range_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_power_saver_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_snooze_time_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_unit_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_vibrate_key;
import static app.tasknearby.yashcreations.com.tasknearby.R.string.pref_voice_alarm_key;

/**
 * Manages the settings/preferences.
 *
 * @author shilpi
 */
public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;

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

        ListPreference mUnitPreference, mSnoozePreference, mVibratePreference;
        RingtonePreference mAlarmTonePreference;
        EditTextPreference mDistancePreference;
        SwitchPreference mVoiceAlarmPreference, mPowerSaverPreference;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            initializeViews();
        }

        /**
         * Attaches a listener so the summary is always updated with the preference value.
         * Also fires the listener once, to initialize the summary (so it shows up before the value
         * is changed.)
         *
         * @param preference
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
         *
         * @param preference
         * @param o
         * @return
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
                    Intent serviceIntent = new Intent(getActivity(), FusedLocationService.class);
                    getActivity().stopService(serviceIntent);

                    // Check if app is enabled.
                    SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String appStatus = defaultPref.getString(getString(R.string.pref_status_key), getString(R.string.pref_status_default));
                    if(appStatus.equals(getString(R.string.pref_status_enabled))){
                        // Start the service again.
                        getActivity().startService(serviceIntent);
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
            mDistancePreference = (EditTextPreference) getPreferenceManager().findPreference
                    (getString(pref_distance_range_key));
            mAlarmTonePreference = (RingtonePreference) getPreferenceManager().findPreference
                    (getString(pref_alarm_tone_key));
            mSnoozePreference = (ListPreference) getPreferenceManager().findPreference(getString
                    (pref_snooze_time_key));
            mVibratePreference = (ListPreference) getPreferenceManager().findPreference(getString
                    (pref_vibrate_key));
            mPowerSaverPreference = (SwitchPreference) getPreferenceManager().findPreference
                    (getString(pref_power_saver_key));
            mVoiceAlarmPreference = (SwitchPreference) getPreferenceManager().findPreference
                    (getString(pref_voice_alarm_key));


            bindPreferenceSummaryToValue(mUnitPreference);
            bindPreferenceSummaryToValue(mDistancePreference);
            bindPreferenceSummaryToValue(mAlarmTonePreference);
            bindPreferenceSummaryToValue(mSnoozePreference);
            bindPreferenceSummaryToValue(mVibratePreference);
            bindPreferenceSummaryToValue(mVoiceAlarmPreference);
            bindPreferenceSummaryToValue(mPowerSaverPreference);
        }
    }
}
