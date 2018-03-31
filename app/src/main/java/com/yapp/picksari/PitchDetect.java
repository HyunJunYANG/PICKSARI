package com.yapp.picksari;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
    final String[] scale = {"3\' 도","3\' 레","3\' 미","3\' 파","3\' 솔","3\' 라","3\' 시"};
    final float[] hz = {200f, 250f, 300f, 600f, 3000f};
    //hz 배열의 index
    int hzIndex = 0;
    //scale배열의 index
    int scaleIndex = 0;
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
                                        Intent intent = new Intent(PitchDetect.this, MainActivity.class); // 수정
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
        timer.schedule(detectTimer, 0, 5000);
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
