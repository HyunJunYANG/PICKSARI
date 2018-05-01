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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    int hzIndex = lowPitchStart;
    //scale배열의 index
    int scaleIndex = lowPitchStart;
    float pitch = 0f;
    Thread thread;
    ImageView manPicture;
    int pitchSuccess;
    private TimerTask detectTimer;
    private TimerTask progressTimer;
    Timer timer;
    Timer progTimer;
    Handler handler = new Handler();

    RelativeLayout waitLayout;
    ImageView waitMan;
    ImageView waitStartBox;
    TextView changeTextView;
    RelativeLayout topLayout;
    RelativeLayout middleLayout;
    ImageView piano;
    TextView pitchText;
    String lowScale;

    float pitchInHz;

    int pitchDetectFlag = 0;
    //실패 감시 flag -> 0:최저음 1회차, 1:최저음 2회차, 2:최고음 1회차, 3:최고음 2회차
    int failFlag = 0;

    SoundPool soundPool;
    int soundId;
    final int soundList[] = {R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8,
            R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8,
            R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8,
            R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8,
            R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8,
            R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8,
            R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8, R.raw.testsound1, R.raw.testsound8};
    int soundIndex = 8;

    ProgressBar progressBar;

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

        waitLayout = findViewById(R.id.waitLayout);
        waitMan = findViewById(R.id.waitMan);
        waitStartBox = findViewById(R.id.waitStartBox);
        changeTextView = findViewById(R.id.changeTextView);
        topLayout = findViewById(R.id.topLayout);
        middleLayout = findViewById(R.id.middleLayout);
        piano = findViewById(R.id.piano);
        progressBar = findViewById(R.id.progressBar);
        manPicture = findViewById(R.id.man_picture);
        ImageView round = findViewById(R.id.round);
        pitchText = findViewById(R.id.pitchtext);

        initProg();
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
        progressBarTimer();

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
                                hzIndex = highPitchStart;
                                scaleIndex = highPitchStart;
                                soundIndex = highPitchStart;

                                topLayout.setVisibility(View.GONE);
                                middleLayout.setVisibility(View.GONE);
                                piano.setVisibility(View.GONE);
                                waitLayout.setVisibility(View.VISIBLE);
                                waitMan.setVisibility(View.VISIBLE);
                                waitStartBox.setVisibility(View.INVISIBLE);
                                changeTextView.setVisibility(View.VISIBLE);
                                changeTextView.bringToFront();
                                soundId = soundPool.load(PitchDetect.this, soundList[soundIndex], 1);

                                Handler lowHighChangerHandler = new Handler();

                                lowHighChangerHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Handler nextChanceHandler = new Handler();
                                        nextChanceHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                topLayout.setVisibility(View.VISIBLE);
                                                middleLayout.setVisibility(View.VISIBLE);
                                                piano.setVisibility(View.VISIBLE);
                                                waitLayout.setVisibility(View.GONE);
                                                waitMan.setVisibility(View.GONE);
                                                manPicture.setImageResource(R.drawable.man_2);
                                                pitchText.setText(scale[scaleIndex]);

                                                soundPool.play(soundId,1.0F, 1.0F,  1,  0,  1.0F);
                                                Handler flagChangeHandler = new Handler();
                                                flagChangeHandler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pitchDetectFlag = 0;
                                                    }
                                                },2000);
                                            }
                                        },1000);
                                    }
                                }, 0);

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
                                            intent.putExtra("lowScaleInfo", lowScale);
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


    public void progressBarTimer() {
        progressTimer = new TimerTask(){ //timerTask는 timer가 일할 내용을 기록하는 객체

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //이곳에 timer가 동작할 task를 작성
                increaseBar(); //timer가 동작할 내용을 갖는 함수 호출
            }

        };
        progTimer = new Timer();
        progTimer.schedule(progressTimer, 0, 100);
    }

    public void initProg() {
        progressBar.setMax(50);
        progressBar.setProgress(0);
    }

    int count = 0;
    public void increaseBar() {
        runOnUiThread( //progressBar는 ui에 해당하므로 runOnUiThread로 컨트롤해야한다
                new Runnable() { //thread구동과 마찬가지로 Runnable을 써주고
                    @Override
                    public void run() { //run을 해준다. 그러나 일반 thread처럼 .start()를 해줄 필요는 없다
                        Log.i(LOG_TAG, "Progress Increase");
                        count = progressBar.getProgress();
                        if(count < 50){
                            count = count + 1;
                        }else if(count == 50){
                            initProg();
                            count = 0;
                        }
                        progressBar.setProgress(count);
                    }
                }
        );
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
                            intent.putExtra("lowScaleInfo", lowScale);
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
                        if (failFlag >= 2) {
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
                                    //최고음 탐색이므로 failFlag를 2로
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
                                } else { //음이 기준보다 낮으면
                                    //Log.d(LOG_TAG, "ThreadSleep Pitch is low.");
                                    pitchSuccess = 2;
                                }
                            }
                        } else if (failFlag < 2) {
                            if (!thread.isInterrupted()) {
                                if (pitchInHz > hz[hzIndex-1] && pitchInHz < hz[hzIndex]) {
                                    Log.d(LOG_TAG, String.valueOf(hz[hzIndex]));
                                    pitch = pitchInHz;

                                    //인덱스를 낮춘다
                                    hzIndex = hzIndex - 1;
                                    scaleIndex = scaleIndex - 1;
                                    soundIndex = soundIndex - 1;
                                    soundId = soundPool.load(PitchDetect.this, soundList[soundIndex], 1);
                                    pitchSuccess = 1;
                                    //최저음 탐색이므로 flag를 0으로 놓는다
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
                                } else { //음이 기준보다 높으면
                                    //Log.d(LOG_TAG, "ThreadSleep Pitch is low.");
                                    pitchSuccess = 2;
                                    lowScale = pitchText.getText().toString();
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
        progTimer.cancel();
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
