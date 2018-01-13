package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Displays an image on the screen.
 *
 * @author vermayash8
 */
public class ShowImageActivity extends AppCompatActivity {

    /**
     * Extra constants passed to this activity.
     */
    private static final String EXTRA_PATH_TO_IMAGE = "imageFilePath";
    private static final String EXTRA_TASK_NAME = "taskName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        // Get the extras.
        setActionBar();
        String imagePath = getIntent().getStringExtra(EXTRA_PATH_TO_IMAGE);
        setImage(imagePath);
    }

    private void setActionBar() {
        // Set the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
            String taskName = getIntent().getStringExtra(EXTRA_TASK_NAME);
            actionBar.setTitle(taskName);
        }
    }

    /**
     * Used to get the starting intent for this activity. Automatically sets the extra parameters.
     */
    public static Intent getStartingIntent(Context context, String taskName, String imageFilePath) {
        Intent intent = new Intent(context, ShowImageActivity.class);
        intent.putExtra(EXTRA_TASK_NAME, taskName);
        intent.putExtra(EXTRA_PATH_TO_IMAGE, imageFilePath);
        return intent;
    }

    /**
     * Sets the image to the image view.
     */
    private void setImage(String imagePath) {
        ImageView imageView = findViewById(R.id.imageView);
        Picasso.with(this)
                .load("file://" + imagePath)
                .into(imageView);
    }
}
