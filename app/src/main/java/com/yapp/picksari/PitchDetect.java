package com.yapp.picksari;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class PitchDetect extends AppCompatActivity {

    static String LOG_TAG="PitchDetect";
    float pitch = 0f;
    Thread thread;
    ImageView manPicture;
    boolean pitchSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_detect);

        manPicture = findViewById(R.id.man_picture);

        detect();
    }

    //음역대를 측정하는 함수
    public void detect() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        pitchSuccess = false;

        Log.d("StartaudioDetect", "start");
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pitchInHz > pitch) {
                            Log.d("StartAudioDetect", "start2");
                            pitch = pitchInHz;
                            pitchSuccess=true;

                            manPicture.setImageResource(R.drawable.man_3);
                            //mhandler.sendMessage(mhandler.obtainMessage(1));
                            //sendMessage(pitch);
                        }
                    }
                });
            }
        };

        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);
        thread = new Thread(dispatcher,"Audio Dispatcher");
        thread.start();

        /*

        while(true) {
            if(pitchSuccess==true) {
                try {
                    Log.d(LOG_TAG, "ThreadSleepStart");
                    thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
        Log.d("EndaudioDetect", "End");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    thread.sleep(1000);
                    Log.d("ThreadSleep", "sleep");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("ThreadEnd","ThreadEnd");
            }
        };

        handler = new Handler();
        while(true) {
            handler.postDelayed(runnable, 5000);
        }
*/
    }
}
