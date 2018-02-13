package app.tasknearby.yashcreations.com.tasknearby.utils.alarm;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.Locale;

import app.tasknearby.yashcreations.com.tasknearby.R;
import app.tasknearby.yashcreations.com.tasknearby.models.LocationModel;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;

/**
 * Voice alerts(reminders).
 *
 * @author shilpi
 */

public class VoiceAlarmRinger implements TextToSpeech.OnInitListener {

    public static final String TAG = VoiceAlarmRinger.class.getSimpleName();
    private TextToSpeech mTts;
    private TaskModel mTask;
    private LocationModel mLocation;
    private Context mContext;

    public VoiceAlarmRinger(Context context, TaskModel task, LocationModel location) {
        mContext = context;
        mTask = task;
        mLocation = location;
    }

    public void startSpeaking() {
        mTts = new TextToSpeech(mContext, this);
    }

    public void stopSpeaking() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }

    /**
     * Called when initialisation of tts is completed.
     *
     * @param status
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech
                    .LANG_NOT_SUPPORTED) {
                Log.e(TAG, Locale.getDefault().getLanguage() + " Language is not supported");
                showSnackbar();

            }
            speakOut();
        } else {
            Log.e(TAG, "Initialization Failed!");
            showSnackbar();
        }
    }

    private void speakOut() {
        // Text to speak.
        String pause = "... ";
        String text = "Task NearBy " + mContext.getString(R.string.reminder) + pause
                + mTask.getTaskName() + pause + " at " + mLocation.getPlaceName() + pause;
        // Add note.
        if (mTask.getNote() != null) {
            text += mTask.getNote();
        }
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Shows snackbar to set google text to speech settings.
     */
    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(((Activity) mContext).findViewById(android.R.id
                .content), mContext.getString(R.string
                .error_tts), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
