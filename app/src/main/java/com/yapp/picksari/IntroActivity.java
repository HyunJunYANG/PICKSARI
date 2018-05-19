package com.yapp.picksari;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

public class IntroActivity extends AppCompatActivity {

    private Handler handler;
    static String LOG_TAG="IntroActivity";
    private SharedPreferences sharedpreferences;


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

        sharedpreferences = getSharedPreferences("Pref", MODE_PRIVATE);
        boolean isFirst = sharedpreferences.getBoolean("isFirst", true);
        if (isFirst) {
            setContentView(R.layout.activity_intro);

            handler = new Handler();
            handler.postDelayed(runnable, 1000); //1초 지연
        }
        else {
            String scaleInfo = sharedpreferences.getString("scaleInfo", "Fail");
            String scaleInfotwo = sharedpreferences.getString("scaleInfotwo", "Fail");

            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            intent.putExtra("scaleInfo", scaleInfo.toString());
            intent.putExtra("scaleInfotwo", scaleInfotwo.toString());

            startActivity(intent);
        }

    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
