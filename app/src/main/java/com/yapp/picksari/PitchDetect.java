package com.yapp.picksari;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class PitchDetect extends AppCompatActivity {

    static String LOG_TAG="PitchDetect";
    static int highPitchStart = 7;
    static int lowPitchStart = 10;
    final String[] scale = {"1\' 도","1\' 레","1\' 미","1\' 파","1\' 솔","1\' 라","1\' 시",
            "2\' 도","2\' 레","2\' 미","2\' 파","2\' 솔","2\' 라","2\' 시",
            "3\' 도","3\' 레","3\' 미","3\' 파","3\' 솔","3\' 라","3\' 시",
            "4\' 도","4\' 레","4\' 미","4\' 파","4\' 솔","4\' 라","4\' 시"};
    final float[] hz = {130.81f, 146.83f, 164.81f, 174.61f, 196f, 220f, 246.94f,
            261.63f, 293.66f, 329.63f, 349.23f, 392f, 440f, 493.88f,
            523.25f, 587.33f, 659.25f, 698.46f, 783.99f, 880f, 987.77f,
            1046.5f, 1174.66f, 1318.51f, 1396.91f, 1567.98f, 1760f, 1975.53f};
    //hz 배열의 index
    int hzIndex = highPitchStart;
    //scale배열의 index
    int scaleIndex = highPitchStart;
    float pitch = 0f;
    Thread thread;
    ImageView manPicture;
    int pitchSuccess;
    private TimerTask detectTimer;
    Timer timer;
    Handler handler = new Handler();
    TextView pitchText;

    //postDelay 그림변경
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //Log.d(LOG_TAG,"pitchSuccessChange");
            manPicture.setImageResource(R.drawable.man_2);
        }
    };

    /*사운드 플레이를 위한 코드 실험안해봄 쓰레드가 돌아갈 때마다 소리나지 않게 하기 위한 추가 코딩 필요
    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(8).build();
        }
        else {
            soundPool = new SoundPool(8, AudioManager.STREAM_NOTIFICATION, 0);
        }

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1f, 1f, 0, 0, 1f);
            }
        });
    }
  */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_detect);

        //안꺼지게
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        manPicture = findViewById(R.id.man_picture);
        ImageView round = findViewById(R.id.round);
        pitchText = findViewById(R.id.pitchtext);

        round.bringToFront();
        pitchText.bringToFront();
        detectTime();
        detect();
    }


    //타이머 - 일정 시간마다 실패를 체크한다(5초)
    public void detectTime() {
        detectTimer = new TimerTask() {
            @Override
            public void run() {
                PitchDetect.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(LOG_TAG, "Timer start");

                        /* 사운드 플레이를 위한 코드 실험안해봄 쓰레드가 돌아갈 때마다 소리나지 않게 하기 위한 추가 코딩 필요
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .build();
                            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(8).build();
                        }
                        else {
                            soundPool = new SoundPool(8, AudioManager.STREAM_NOTIFICATION, 0);
                        }

                        if (soundIds[soundIndex] == 0) {
                            // ex, rawSound == R.raw.mi (미)
                             soundIds[soundIndex] = soundPool.load(MainActivity.this, rawSound, 1);
                        } else {
                             soundPool.play(soundIds[soundIndex], 1f, 1f, 0, 0, 1f);
                        }

                        */
                        //실패하면 그림을 바꾸고 종료함
                        if(pitchSuccess == 2) {
                            try {
                                manPicture.setImageResource(R.drawable.man_4);
                                //Thread.sleep(1000);
                                timer.cancel();
                                thread.interrupted();
                                Handler handler = new Handler();
                                //1초 딜레이
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(PitchDetect.this, PitchResult.class); // 수정
                                        intent.putExtra("scaleInfo", pitchText.getText().toString());
                                        startActivity(intent);
                                        System.exit(0);
                                    }
                                },1000);

                                //finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(detectTimer, 5000, 5000);
    }


    //음역대를 측정하는 함수
    public void detect() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        //0은 초기 1은 성공 2는 실패
        pitchSuccess = 0;

        Log.d(LOG_TAG, "start");

        //PitchDetect Thread
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void run() {
                        //음이 기준보다 높으면 이미지바꾸고 sleep
                        if(!thread.isInterrupted()) {
                            if (pitchInHz > hz[hzIndex]) {
                                Log.d(LOG_TAG, String.valueOf(hz[hzIndex]));
                                pitchText.setText(scale[scaleIndex]);
                                pitch = pitchInHz;

                                //인덱스를 늘린다
                                hzIndex = hzIndex + 1;
                                scaleIndex = scaleIndex + 1;
                                pitchSuccess = 1;

                                //타이머를 죽인다. 성공하면 타이머 리셋해야하기 때문
                                timer.cancel();

                                manPicture.setImageResource(R.drawable.man_3);
                                //PostDelay 그림을 바꾸기 위한 딜레이
                                try {
                                    Log.d(LOG_TAG, "ThreadSleepStart");
                                    //thread.sleep(1000);
                                    handler.postDelayed(runnable, 1000);
                                    //타이머 리셋
                                    detectTime();
                                    //thread.interrupt();
                                    //Log.d(LOG_TAG, "ThreadSleepInterrupted");
                                    Log.d(LOG_TAG, "ThreadSleepRestart");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else { //음이 기준보다 낮으면
                                //Log.d(LOG_TAG, "ThreadSleep Pitch is low.");
                                pitchSuccess = 2;
                            }
                        }
                    }
                });
            }
        };

        //선언
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);

        thread = new Thread(dispatcher,"Audio Dispatcher");
        thread.start();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        //타이머 캔슬과 쓰레드 캔슬
        timer.cancel();
        thread.interrupt();

        /*사운드풀 제거를 위한 코드 실험안해봄
        for (int i = 0 ; i < soundIds.length ; i++) {
            soundIds[i] = 0;
        }

        soundPool.release();
        soundPool = null;
    */
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        //타이머 캔슬과 쓰레드 캔슬
        timer.cancel();
        thread.interrupt();
        super.onDestroy();
    }
}
