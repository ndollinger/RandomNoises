package com.nickdollinger.randomnoises;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by ndolling on 12/9/13.
 * For Great Justice
 */
public class PlayFragment extends Fragment {
    //OnPlayClickedListener playClickedListener;
    //OnStopClickedListener stopClickedListener;

    private SoundPool mSoundPool;
    private AudioManager mAudioManager;
    private ImageView playingIndicator;
    private int a440;
    private int mCurrentStreamID = 0;
    final static int LOOP_FOREVER = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.play_fragment, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try{
//            //playClickedListener = (OnPlayClickedListener) activity;
//            //stopClickedListener = (OnStopClickedListener) activity;
//        }
//        catch (ClassCastException e){
//            throw new ClassCastException(activity.toString() + " must implement button listeners");
//        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup SoundPool, do this here since we need Activity Context
        Context context = getActivity();
        // Instantiate the AudioManager for use later
        mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        // Create and load up the Sound Pool
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        a440 = mSoundPool.load(context, R.raw.a440, 1);

        // Get the ImageView that will tell us if sound is playing in order to be used later
        playingIndicator = (ImageView) getView().findViewById(R.id.play_indicator);
        playingIndicator.setImageResource(R.drawable.volume_on);
        playingIndicator.setVisibility(View.INVISIBLE);

        // Setup the Button Handlers
        final Button playButton = (Button) getView().findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start playing a sound
                float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                //streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                mCurrentStreamID = mSoundPool.play(a440, streamVolume, streamVolume, 1, LOOP_FOREVER, 1f);
                // Show the "sound is playing" indicator
                // TODO: Does work on UI thread, consider changing, Also, do a better job of handling
                if(playingIndicator != null){
                    playingIndicator.setVisibility(View.VISIBLE);
                }
            }
        });
        final Button stopButton = (Button) getView().findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Stop the sound that's playing above...
                Might be a REALLY bad idea to just leave the above looping
                */
                mSoundPool.stop(mCurrentStreamID);
                if(playingIndicator != null){
                    playingIndicator.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        /* Stop the sound if the app is no longer being shown */
        mSoundPool.stop(mCurrentStreamID);
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        /* Tear down the SoundPool.  Will be recreated when fragment is recreated */
        mSoundPool.stop(mCurrentStreamID);
        mSoundPool.release();
    }
}
