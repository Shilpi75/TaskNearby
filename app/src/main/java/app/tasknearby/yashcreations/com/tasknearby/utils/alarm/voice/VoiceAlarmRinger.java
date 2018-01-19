package app.tasknearby.yashcreations.com.tasknearby.utils.alarm.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

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
            }
            speakOut();
        } else {
            Log.e(TAG, "Initialization Failed!");
        }


    }

    private void speakOut() {
        // Text to speak.
        String pause = "... ";
        String text = "Task NearBy Reminder" + pause + mTask.getTaskName() + pause + " at " +
                mLocation.getPlaceName() + pause;
        // Add note.
        if (mTask.getNote() != null) {
            text += mTask.getNote();
        }
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
