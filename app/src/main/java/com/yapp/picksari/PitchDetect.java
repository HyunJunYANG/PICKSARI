package com.yapp.picksari;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
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
    static int highPitchStart = 15;
    static int lowPitchStart = 8;
    final String[] scale = {"","0\' 도","0\' 레","0\' 미","0\' 파","0\' 솔","0\' 라","0\' 시",
            "1\' 도","1\' 레","1\' 미","1\' 파","1\' 솔","1\' 라","1\' 시",
            "2\' 도","2\' 레","2\' 미","2\' 파","2\' 솔","2\' 라","2\' 시",
            "3\' 도","3\' 레","3\' 미","3\' 파","3\' 솔","3\' 라","3\' 시",
            "4\' 도","4\' 레","4\' 미","4\' 파","4\' 솔","4\' 라","4\' 시",""};
    final float[] hz = {1f, 65.41f, 73.42f, 82.41f, 87.31f, 98.00f, 110.00f, 123.47f,
            130.81f, 146.83f, 164.81f, 174.61f, 196f, 220f, 246.94f,
            261.63f, 293.66f, 329.63f, 349.23f, 392f, 440f, 493.88f,
            523.25f, 587.33f, 659.25f, 698.46f, 783.99f, 880f, 987.77f,
            1046.5f, 1174.66f, 1318.51f, 1396.91f, 1567.98f, 1760f, 1975.53f, 10000f};
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
    String highScale;

    float pitchInHz;

    int pitchDetectFlag = 0;
    //실패 감시 flag -> 0:최고음 1회차, 1:최고음 2회차, 2:최저음 1회차, 3:최저음 2회차
    int failFlag = 0;

    SoundPool soundPool;
    int soundId;
    final int soundList[] = {R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8};
    int soundIndex = 0;

    //postDelay 그림변경
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //Log.d(LOG_TAG,"pitchSuccessChange");
            soundPool.play(soundId,1.0F, 1.0F,  1,  0,  1.0F);
            manPicture.setImageResource(R.drawable.man_2);
            Handler flagChangeHandler = new Handler();
            flagChangeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pitchDetectFlag = 0;
                }
            },2000);
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build();
        }
        else {
            soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        }

        soundId = soundPool.load(this,soundList[soundIndex],1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_detect);

        //안꺼지게
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        manPicture = findViewById(R.id.man_picture);
        ImageView round = findViewById(R.id.round);
        pitchText = findViewById(R.id.pitchtext);

        pitchText.setText(scale[scaleIndex]);
        round.bringToFront();
        pitchText.bringToFront();

        Handler soundPoolHandler = new Handler();
        soundPoolHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundPool.play(soundId,1.0F, 1.0F,  1,  0,  1.0F);
            }
        },200);

        detectTime();

        Handler detectHandler = new Handler();
        detectHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                detect();
            }
        },500);

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

                        //실패하면 그림을 바꾸고 종료함
                        if(pitchSuccess == 2) {
                            pitchDetectFlag = 1;
                            manPicture.setImageResource(R.drawable.man_4);

                            if(failFlag == 0) {
                                failFlag = 1;
                                Handler nextChanceHandler = new Handler();
                                nextChanceHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pitchDetectFlag = 0;
                                        manPicture.setImageResource(R.drawable.man_2);
                                    }
                                },1000);
                            } else if(failFlag == 1) {
                                failFlag = 2;
                                hzIndex = lowPitchStart;
                                scaleIndex = lowPitchStart;
                                Handler nextChanceHandler = new Handler();
                                nextChanceHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pitchDetectFlag = 0;
                                        manPicture.setImageResource(R.drawable.man_2);
                                        pitchText.setText(scale[scaleIndex]);
                                    }
                                },1000);
                            } else if(failFlag == 2) {
                                failFlag = 3;
                                Handler nextChanceHandler = new Handler();
                                nextChanceHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pitchDetectFlag = 0;
                                        manPicture.setImageResource(R.drawable.man_2);
                                    }
                                },1000);
                            } else {
                                try {
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
                                            intent.putExtra("highScaleInfo", highScale);
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
                //final float pitchInHz = result.getPitch();
                if(scaleIndex == 0 || scaleIndex == 36) {
                    Handler handler = new Handler();
                    //1초 딜레이
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(PitchDetect.this, PitchResult.class); // 수정
                            intent.putExtra("scaleInfo", pitchText.getText().toString());
                            intent.putExtra("highScaleInfo", highScale);
                            startActivity(intent);
                            System.exit(0);
                        }
                    },1000);
                }
                if(pitchDetectFlag == 0) {
                    pitchInHz = result.getPitch();
                } else {
                    pitchInHz = 0;
                }
                runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void run() {
                        if (failFlag < 2) {
                            //음이 기준보다 높으면 이미지바꾸고 sleep
                            if (!thread.isInterrupted()) {
                                if (pitchInHz > hz[hzIndex]) {
                                    Log.d(LOG_TAG, String.valueOf(hz[hzIndex]));
                                    pitch = pitchInHz;

                                    //인덱스를 늘린다
                                    hzIndex = hzIndex + 1;
                                    scaleIndex = scaleIndex + 1;
                                    soundIndex = soundIndex + 1;
                                    soundId = soundPool.load(PitchDetect.this, soundList[soundIndex], 1);
                                    pitchSuccess = 1;
                                    failFlag = 0;

                                    pitchText.setText(scale[scaleIndex]);
                                    //타이머를 죽인다. 성공하면 타이머 리셋해야하기 때문
                                    timer.cancel();

                                    pitchDetectFlag = 1;
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
                                    highScale = pitchText.getText().toString();
                                }
                            }
                        } else if (failFlag >= 2) {
                            if (!thread.isInterrupted()) {
                                if (pitchInHz > hz[hzIndex-1] && pitchInHz < hz[hzIndex]) {
                                    Log.d(LOG_TAG, String.valueOf(hz[hzIndex]));
                                    pitch = pitchInHz;

                                    //인덱스를 낮춘다
                                    hzIndex = hzIndex - 1;
                                    scaleIndex = scaleIndex - 1;
                                    pitchSuccess = 1;
                                    //최저음 탐색이므로 flag를 2로 놓는다
                                    failFlag = 2;
                                    pitchText.setText(scale[scaleIndex]);

                                    //타이머를 죽인다. 성공하면 타이머 리셋해야하기 때문
                                    timer.cancel();

                                    pitchDetectFlag = 1;
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
                                } else { //음이 기준보다 높으면
                                    //Log.d(LOG_TAG, "ThreadSleep Pitch is low.");
                                    pitchSuccess = 2;
                                }
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

        soundPool.release();
        soundPool = null;
        System.exit(0);

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
