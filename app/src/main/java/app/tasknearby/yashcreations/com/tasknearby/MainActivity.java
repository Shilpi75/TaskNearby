package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import app.tasknearby.yashcreations.com.tasknearby.utils.AppUtils;

/**
 * Shows the list of tasks segregated into categories when the app loads. This activity also
 * contains the switch that will turn the app's service on or off.
 *
 * @author vermayash8
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // To set up the power saver preference if user has updated the app.
        setPowerSaverPreference();
        setupNavDrawer();

        findViewById(R.id.fab).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, TaskCreatorActivity.class)));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new TasksFragment())
                .commit();
    }

    private void setupNavDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, findViewById(R.id
                .toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_tasks) {
            Log.i(TAG, "All Tasks");
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_feedback) {
            AppUtils.sendFeedbackEmail(this);
        } else if (id == R.id.nav_share) {
            AppUtils.rateApp(this);
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.premium) {
            Log.i(TAG, "Premium");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Sets up the power saver preference if user has updated the app.
     */
    public void setPowerSaverPreference() {
        // Set up power/accuracy preferences.
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!defaultPref.contains(getString(R.string.pref_power_saver_key))) {
            // It means user has updated the app and opening this version for the first time.
            String accuracy = defaultPref.getString(getString(R.string.pref_accuracy_key),
                    getString(R.string.pref_accuracy_default));
                    
            SharedPreferences.Editor editor = defaultPref.edit();
            if (accuracy.equals(getString(R.string.pref_accuracy_balanced))) {
                // Set power saver mode.
                editor.putBoolean(getString(R.string.pref_power_saver_key), true);
            } else {
                editor.putBoolean(getString(R.string.pref_power_saver_key), false);
            }
            editor.apply();
        }
    }
}
