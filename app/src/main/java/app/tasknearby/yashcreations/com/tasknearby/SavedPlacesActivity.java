package app.tasknearby.yashcreations.com.tasknearby;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;

/**
 * Responsible for showing the user a list of saved locations to allow him to pick one. The
 * location selected by the user is returned to the calling activity. The user can also choose to
 * delete locations.
 *
 * @author vermayash8
 */
public class SavedPlacesActivity extends AppCompatActivity {

    private static final String TAG = SavedPlacesActivity.class.getSimpleName();

    /**
     * Constant that defines the EXTRA field for the intent in which locationId will be passed to
     * the calling activity.
     */
    public static final String EXTRA_LOCATION_ID = "locationId";

    /**
     * Used when no saved locations are present and user decides to use place picker.
     */
    public static final int RESULT_USE_PLACE_PICKER = 1234;

    /**
     * Adapter for RecyclerView.
     */
    private SavedPlacesAdapter mSavedPlacesAdapter;

    /**
     * For the database operations.
     */
    private TaskRepository mTaskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_places);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Get the list of saved locations.
        mTaskRepository = new TaskRepository(getApplicationContext());
        List<LocationModel> locations = mTaskRepository.getAllLocations();
        // Sort in descending order of use count.
        Collections.sort(locations, (o1, o2) -> o2.getUseCount() - o1.getUseCount());
        // Create the RecyclerView adapter.
        mSavedPlacesAdapter = new SavedPlacesAdapter(locations);
        // Set the adapter to the recyclerView.
        RecyclerView recyclerView = findViewById(R.id.recycler_saved_places);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mSavedPlacesAdapter);
        // Initial setting of empty location view.
        setEmptyView(locations.size());
        // set the place picker button when no locations.
        findViewById(R.id.button_pick_from_map).setOnClickListener(v -> {
            this.setResult(RESULT_USE_PLACE_PICKER, getIntent());
            finish();
        });
    }

    /**
     * Shows the empty view if numLocations == 0
     *
     * @param numLocations the number of locations being shown.
     */
    private void setEmptyView(int numLocations) {
        if (numLocations != 0) {
            findViewById(R.id.button_pick_from_map).setVisibility(View.GONE);
            findViewById(R.id.text_no_locations).setVisibility(View.GONE);
            findViewById(R.id.recycler_saved_places).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.button_pick_from_map).setVisibility(View.VISIBLE);
            findViewById(R.id.text_no_locations).setVisibility(View.VISIBLE);
            findViewById(R.id.recycler_saved_places).setVisibility(View.GONE);
        }
    }

    /**
     * Deletes the location from the database after the user's confirmation.
     */
    private void deleteLocation(LocationModel location) {
        // Show an alert dialog.
        AlertDialog alertDialog = new AlertDialog.Builder(SavedPlacesActivity.this)
                .setMessage(getString(R.string.saved_places_dialog_msg_delete_place) + location.getPlaceName()
                        + "\"")
                .setPositiveButton(R.string.dialog_action_delete, (dialog, which) -> {
                    location.setIsHidden(1);
                    mTaskRepository.updateLocation(location);
                    mSavedPlacesAdapter.removeLocation(location);
                    setEmptyView(mSavedPlacesAdapter.getItemCount());
                })
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel())
                .create();
        alertDialog.show();
    }

    /**
     * Returns the selected location to the calling activity.
     */
    private void onLocationSelected(LocationModel location) {
        Intent callingIntent = getIntent();
        callingIntent.putExtra(EXTRA_LOCATION_ID, location.getId());
        this.setResult(RESULT_OK, callingIntent);
        finish();
    }


    /**
     * RecyclerView Adapter for the showing the list of saved places.
     */
    private class SavedPlacesAdapter extends RecyclerView.Adapter<SavedPlacesAdapter
            .LocationViewHolder> {

        /**
         * List containing all saved locations.
         */
        private List<LocationModel> mLocations;

        SavedPlacesAdapter(@NonNull List<LocationModel> locations) {
            this.mLocations = locations;
        }

        @Override
        public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.list_item_location, parent, false);
            return new LocationViewHolder(v);
        }

        @Override
        public void onBindViewHolder(LocationViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mLocations.size();
        }

        public void removeLocation(LocationModel location) {
            mLocations.remove(location);
            notifyDataSetChanged();
        }

        /**
         * ViewHolder class for the location list items. Each location list item consists of the
         * location name textView and a delete button.
         * This class also implements OnClickListener interface to get clicks on the views.
         */
        class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView locationNameTv;
            private ImageButton deleteButton;

            /**
             * Constructor.
             */
            LocationViewHolder(View v) {
                super(v);
                locationNameTv = v.findViewById(R.id.text_location_name);
                deleteButton = v.findViewById(R.id.button_delete);

                locationNameTv.setOnClickListener(this);
                deleteButton.setOnClickListener(this);
            }

            /**
             * Sets the data to the view.
             */
            void bind(int position) {
                locationNameTv.setText(mLocations.get(position).getPlaceName());
            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                switch (v.getId()) {
                    case R.id.button_delete:
                        // User pressed the delete button.
                        deleteLocation(mLocations.get(position));
                        break;
                    case R.id.text_location_name:
                        // Location has been selected.
                        onLocationSelected(mLocations.get(position));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
