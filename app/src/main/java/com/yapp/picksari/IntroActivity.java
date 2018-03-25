package com.yapp.picksari;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class IntroActivity extends AppCompatActivity {

    private Handler handler;
    static String LOG_TAG="IntroActivity";

    //postDealyed로 실행되는 쓰레드, PitchDetectStart 액티비티로 넘어간다
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(IntroActivity.this, PitchDetectStart.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //애니메이션처리
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        handler = new Handler();
        handler.postDelayed(runnable, 1000); //1초 지연
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
