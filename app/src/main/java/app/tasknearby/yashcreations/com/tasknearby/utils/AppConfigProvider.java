package app.tasknearby.yashcreations.com.tasknearby.utils;

import android.content.Context;

import app.tasknearby.yashcreations.com.tasknearby.R;

public class AppConfigProvider {

    public static String getPlacesApiKey(final Context appContext) {
        return appContext.getString(R.string.places_api_key);
    }

}
