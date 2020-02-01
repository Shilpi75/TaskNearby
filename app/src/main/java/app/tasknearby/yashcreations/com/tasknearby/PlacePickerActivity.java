package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.Locale;

import app.tasknearby.yashcreations.com.tasknearby.utils.AppConfigProvider;
import app.tasknearby.yashcreations.com.tasknearby.utils.firebase.AnalyticsConstants;

/**
 * Displays an activity that provides functionality to pick up places.
 */
public class PlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = PlacePickerActivity.class.getSimpleName();

    /**
     * Keys to which corresponding inputs to this activity will be mapped.
     */
    private static final String KEY_PARAM_INITIAL_LATITUDE = "paramInitialLatitude";
    private static final String KEY_PARAM_INITIAL_LONGITUDE = "paramInitialLongitude";
    private static final String KEY_PARAM_INITIAL_PLACE_NAME = "paramInitialPlaceName";

    /**
     * Key to which it'll map the resultant place object.
     */
    public static final String KEY_RESULT_SELECTED_PLACE = "placePickerChosenPlace";

    /**
     * The zoom level at which it'll open the map by default.
     */
    private static final int DEFAULT_ZOOM_LEVEL = 15;

    /**
     * If the user selects a location using autocomplete query and then moves the map, this defines the max distance in
     * meters till which we can refer to the arbitrary place with the searched place's name.
     */
    private static final float MAX_DISTANCE_FOR_PLACE_NAME_VALIDITY_IN_METERS = 250;

    /**
     * Publishes the metrics to firebase which will be analyzed using Firebase analytics.
     */
    private FirebaseAnalytics firebaseAnalytics;

    /**
     * Object that represents the map which is loaded.
     */
    private GoogleMap googleMap;

    /**
     * Provides UI for searching the place with autocomplete using suggestions.
     */
    private AutocompleteSupportFragment autocompleteFragment;

    /**
     * The position at which map is centered when this activity starts.
     */
    private LatLng initialPosition;

    /**
     * The last "name-able" place that was selected by the user.
     */
    private Place selectedPlace;

    /**
     * The select this location button.
     */
    private Button selectButton;

    /**
     * Constructs the invoking intent for this activity by setting the optional parameters in the intent. Abstracts the
     * additional complexity of populating in the corresponding fields from the caller.
     *
     * @param context          the context of the calling activity.
     * @param initialPosition  the position at which map will be centered. Note that search results will also be biased
     *                         to this location.
     * @param initialPlaceName The name with which we can refer to the initial places. Useful when editing a saved task
     *                         so that we don't end up changing the name.
     * @return Intent that can be directly passed to startActivityForResult method.
     */
    public static Intent createStartingIntent(final Context context, final LatLng initialPosition,
                                              final String initialPlaceName) {
        Intent activityStartingIntent = new Intent(context, PlacePickerActivity.class);
        if (null != initialPosition) {
            activityStartingIntent.putExtra(KEY_PARAM_INITIAL_LATITUDE, initialPosition.latitude);
            activityStartingIntent.putExtra(KEY_PARAM_INITIAL_LONGITUDE, initialPosition.longitude);
            activityStartingIntent.putExtra(KEY_PARAM_INITIAL_PLACE_NAME, initialPlaceName);
        }
        return activityStartingIntent;
    }

    /**
     * Provides invoking intent for this activity in the case when map is to be opened at the default location (current
     * location).
     *
     * @param context the context of the calling activity.
     * @return Intent that can be directly passed to startActivityForResult method.
     */
    public static Intent createStartingIntent(final Context context) {
        return createStartingIntent(context, null, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        selectButton = findViewById(R.id.selectButton);
        selectButton.setOnClickListener(v -> select());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (null != mapFragment) {
            mapFragment.getMapAsync(this);
        }
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), AppConfigProvider.getPlacesApiKey(getApplicationContext()));
        }
        setupAutocompleteFragment();
        final Intent placePickerIntent = getIntent();
        if (placePickerIntent.hasExtra(KEY_PARAM_INITIAL_LATITUDE) && placePickerIntent.hasExtra(KEY_PARAM_INITIAL_LONGITUDE)) {
            initialPosition = new LatLng(placePickerIntent.getDoubleExtra(KEY_PARAM_INITIAL_LATITUDE, 0),
                    placePickerIntent.getDoubleExtra(KEY_PARAM_INITIAL_LONGITUDE, 0));
            setLocationBias(autocompleteFragment, initialPosition);
            selectedPlace = Place.builder().setName(placePickerIntent.getStringExtra(KEY_PARAM_INITIAL_PLACE_NAME))
                    .setLatLng(initialPosition)
                    .build();
        } else {
            // No position was passed, so center the map at current location.
            // Now there will be two async processes running – loading GoogleMap and getting current location.
            FusedLocationProviderClient fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(getApplicationContext());
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(currentLocation -> {
                if (null != currentLocation) {
                    initialPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    setLocationBias(autocompleteFragment, initialPosition);
                    if (null != googleMap) {
                        // in a case where current location arrives after the map load.
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, DEFAULT_ZOOM_LEVEL));
                    }
                }
            });
        }
    }

    /**
     * Called on select button click.
     */
    private void select() {
        LatLng mapPosition = new LatLng(0, 0);
        if (googleMap != null) {
            mapPosition = googleMap.getCameraPosition().target;
        }
        String placeName = null;
        if (selectedPlace != null && selectedPlace.getName() != null && selectedPlace.getLatLng() != null) {
            // Deciding whether we can use the name from last location that we had a name for.
            float[] distanceMapPosToNamedPlace = new float[1];
            Location.distanceBetween(mapPosition.latitude, mapPosition.longitude, selectedPlace.getLatLng().latitude,
                    selectedPlace.getLatLng().longitude, distanceMapPosToNamedPlace);
            if (distanceMapPosToNamedPlace[0] < MAX_DISTANCE_FOR_PLACE_NAME_VALIDITY_IN_METERS) {
                placeName = selectedPlace.getName();
            }
        }
        if (null == placeName) {
            placeName = String.format(Locale.getDefault(), getString(R.string.place_picker_dialog_default_name_format),
                    mapPosition.latitude, mapPosition.longitude);
        }
        showConfirmationDialog(placeName, mapPosition);
    }

    /**
     * Displays the confirmation alert to the user for confirming the name. Also allows the user to provide alternate
     * nick name.
     */
    private void showConfirmationDialog(final String placeName, final LatLng mapPosition) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.place_picker_dialog_heading));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_save_selected_location_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText locationNameInput = dialogView.findViewById(R.id.locationNameInput);
        locationNameInput.setText(placeName);
        final ImageView editButton = dialogView.findViewById(R.id.confirmLocationEditNameButton);
        editButton.setOnClickListener(v -> {
            firebaseAnalytics.logEvent(AnalyticsConstants.PLACE_PICKER_DIALOG_EDIT_BUTTON, null);
            locationNameInput.setSelectAllOnFocus(true);
            locationNameInput.requestFocus();
        });
        locationNameInput.setOnFocusChangeListener((v, hasFocus) -> locationNameInput.post(() -> {
            InputMethodManager imm = (InputMethodManager) PlacePickerActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(locationNameInput, InputMethodManager.SHOW_IMPLICIT);
        }));
        dialogBuilder.setNegativeButton(getString(R.string.place_picker_dialog_change_location_text),
                (dialog, which) -> dialog.dismiss());
        dialogBuilder.setPositiveButton(getString(R.string.place_picker_dialog_proceed_text),null);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        Button proceed = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        proceed.setOnClickListener(v -> {
            final String locationName = locationNameInput.getText().toString();
            if (TextUtils.isEmpty(locationName)) {
                locationNameInput.setHintTextColor(ContextCompat.getColor(PlacePickerActivity.this, R.color.red));
                return;
            }
            Place chosenPlace = Place.builder().setName(locationName).setLatLng(mapPosition).build();
            getIntent().putExtra(KEY_RESULT_SELECTED_PLACE, chosenPlace);
            this.setResult(RESULT_OK, getIntent());
            PlacePickerActivity.this.finish();
        });
    }

    private void setupAutocompleteFragment() {
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (null != autocompleteFragment) {
            autocompleteFragment.setHint(getString(R.string.place_picker_search_bar_hint));
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));
            replaceSearchIconWithBack(autocompleteFragment);
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    selectedPlace = place;
                    if (null != googleMap) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.e(TAG, "Encountered an error when user selected a place using place picker.");
                    firebaseAnalytics.logEvent(AnalyticsConstants.PLACE_PICKER_AUTOCOMPLETE_ERROR, null);
                }
            });
        }
    }

    /**
     * Replaces the search icon present in {@link AutocompleteSupportFragment} by default with a back arrow. This is
     * because we don't have an actionbar and back button should be there. Uses a hacky way but makes sure that
     * views are there.
     */
    private void replaceSearchIconWithBack(AutocompleteSupportFragment autocompleteSupportFragment) {
        View view = autocompleteSupportFragment.getView();
        if (view instanceof LinearLayout) {
            if (((LinearLayout) view).getChildAt(0) instanceof ImageView) {
                ImageView searchIconView = (ImageView) ((LinearLayout) view).getChildAt(0);
                searchIconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_round_arrow_back_24px));
                searchIconView.setOnClickListener(v -> PlacePickerActivity.this.onBackPressed());
            }
        }
    }

    /**
     * Provides a bias to the returned results to a particular location.
     *
     * @param autocompleteFragment the autocomplete fragment to which bias will be set.
     * @param biasLocation the location to which results will be biased.
     */
    private void setLocationBias(final AutocompleteSupportFragment autocompleteFragment, final LatLng biasLocation) {
        if (null != biasLocation) {
            final LatLng southWest = new LatLng(biasLocation.latitude - 0.001, biasLocation.longitude - 0.001);
            final LatLng northEast = new LatLng(biasLocation.latitude + 0.001, biasLocation.longitude + 0.001);
            autocompleteFragment.setLocationBias(RectangularBounds.newInstance(southWest, northEast));
        }
    }

    /**
     * Triggered when google map loads successfully asynchronously.
     *
     * @param googleMap the loaded map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (null == googleMap) {
            Log.e(TAG, "Failed to load google map.");
            firebaseAnalytics.logEvent(AnalyticsConstants.PLACE_PICKER_MAP_LOAD_RETURNED_NULL, null);
            return;
        }
        this.googleMap = googleMap;
        // Enable the button only after map load succeeds. Helpful on slow connections.
        selectButton.setEnabled(true);
        googleMap.setMyLocationEnabled(true);
        if (null != initialPosition) {
            // Won't be triggered when map load completes before initialLocation is populated.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, DEFAULT_ZOOM_LEVEL));
        }
        googleMap.clear();
    }


}
