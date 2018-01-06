package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Shows the list of tasks segregated into categories when the app loads. This activity also
 * contains the switch that will turn the app's service on or off.
 *
 * @author vermayash8
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        // For debugging.
        fab.setOnClickListener(view -> startActivity(new Intent(this, AlarmActivity.class)));

//        startService(new Intent(this, FusedLocationService.class));

    }

}
