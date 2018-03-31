package com.yapp.picksari;

import android.util.Log;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by 양현준 on 2018-03-17.
 */

public class TarsosDSP {

    Thread thread;
    Boolean bool = true;

    float pitch = 200f;

    public TarsosDSP() {

    }

    public boolean detect() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        Log.d("StartaudioDetect", "start");
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                 thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(pitchInHz > pitch) {
                            Log.d("StartAudioDetect", "start2");
                            pitch = pitchInHz;
                            bool = true;
                            //sendMessage(pitch);
                            /*
                            text = (TextView) findViewById(R.id.textView1);
                            text.setText("" + pitchInHz);
                            pitchChange();
                            */
                        } else {
                            bool = false;
                        }
                    }
                });
            }
        };

        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);
        thread = new Thread(dispatcher,"Audio Dispatcher");
        thread.start();

        return bool;
    }

    public void stop() {
        thread.interrupt();
        Log.d("ThreadEnd","ThreadEnd");
    }
}
