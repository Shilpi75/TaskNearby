package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import app.tasknearby.yashcreations.com.tasknearby.utils.firebase.AnalyticsConstants;

/**
 * Shows a list of premium features and provides an option to purchase them.
 *
 * @author vermayash8
 */
public class UpgradeActivity extends AppCompatActivity {

    private static final String TAG = UpgradeActivity.class.getSimpleName();

    private String[] mItemNames;

    private final int[] mItemIcons = {
            // These are in order as mentioned in the itemNames array.
            R.drawable.ic_pro_add_image,
            R.drawable.ic_pro_note,
            R.drawable.ic_pro_time_range,
            R.drawable.ic_pro_date_interval,
            R.drawable.ic_pro_snooze,
            R.drawable.ic_pro_voice_alerts,
            R.drawable.ic_pro_route_generate
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        mItemNames = getResources().getStringArray(R.array.upgrade_premium_items);

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new PremiumAdapter(this));
        listView.setDividerHeight(0);

        findViewById(R.id.button_cross).setOnClickListener(v -> finish());
        findViewById(R.id.button_purchase).setOnClickListener(v -> {
            // This analytic will let us know if the users are dropping off on seeing the price.
            analytics.logEvent(AnalyticsConstants.PREMIUM_DIALOG_USER_CLICKED_BUY, new Bundle());
            // TODO: Call the Billing APIs here. The following code is here just for testing.
            dummyPurchase();

            // Ideally, we should display a ThankYou message over here.
            finish();
        });

        analytics.logEvent(AnalyticsConstants.PREMIUM_DIALOG_SHOWN, new Bundle());
    }

    /**
     * Allows us to start this activity just by calling <code> UpgradeActivity.show(); </code>
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, UpgradeActivity.class));
    }

    // TODO: Remove after the billing APIs have been integrated.
    private void dummyPurchase() {
        // Toggles premium status on button click.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                (UpgradeActivity.this);
        boolean currentPremiumStatus = prefs.getBoolean(getString(R.string
                .pref_is_premium_user_key), false);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean(getString(R.string.pref_is_premium_user_key), !currentPremiumStatus);
        ed.apply();
        if (currentPremiumStatus) {
            Toast.makeText(this, "Converted to NON-premium", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Converted to premium", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adapter class for setting the premium items list.
     */
    private class PremiumAdapter extends ArrayAdapter<String> {

        PremiumAdapter(@NonNull Context context) {
            super(context, R.layout.list_item_premium_items, mItemNames);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.list_item_premium_items, parent, false);
            }
            TextView itemNameTv = v.findViewById(R.id.text_item_name);
            ImageView imageView = v.findViewById(R.id.icon_image);
            itemNameTv.setText(mItemNames[position]);
            imageView.setImageResource(mItemIcons[position]);
            return v;
        }
    }
}
