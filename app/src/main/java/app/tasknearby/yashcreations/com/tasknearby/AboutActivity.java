package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.tasknearby.yashcreations.com.tasknearby.utils.AppUtils;

/**
 * Displays the about screen for the app.
 *
 * @author shilpi
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView feedbackTv;
    private FloatingActionButton rateFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setActionBar();
        initiateViews();
    }

    public void setActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_about));
    }

    public void initiateViews() {
        feedbackTv = findViewById(R.id.text_feedback);
        rateFab = findViewById(R.id.fab_rate);

        // Set on click listeners.
        feedbackTv.setOnClickListener(this);
        rateFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_feedback:
                AppUtils.sendFeedbackEmail(this);
                break;

            case R.id.fab_rate:
                AppUtils.rateApp(this);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                String packageName = getPackageName();
                // Generated using GooglePlayUrlBuilder on Google analytics website.
                String referrer = "&referrer=utm_source%3Dshareapp";
                String appUrl = getString(R.string.play_store_base_url) + packageName + referrer;

                // Share message.
                String shareMessage = String.format(getString(R.string.share_message), appUrl);
                // Share intent.
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                if (shareIntent.resolveActivity(AboutActivity.this.getPackageManager()) != null)
                    startActivity(shareIntent);
                else
                    Toast.makeText(AboutActivity.this, "No app found to share the app!", Toast
                            .LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
