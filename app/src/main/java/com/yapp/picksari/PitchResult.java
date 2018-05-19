package com.yapp.picksari;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class PitchResult extends AppCompatActivity {
    ImageView resultImage;
    Handler handler;
    TextView mainText;
    String scaleInfo;
    String lowScaleInfo;

    //postDealyed로 실행되는 쓰레드, 메인액티비티로 넘어간다
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(PitchResult.this, MainActivity.class);
            intent.putExtra("scaleInfo", scaleInfo.toString()); // max 값
            intent.putExtra("scaleInfotwo", lowScaleInfo.toString()); // min 값

            SharedPreferences sharedpreferences = getSharedPreferences("Pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("scaleInfo", scaleInfo.toString());
            editor.putString("scaleInfotwo", lowScaleInfo.toString());
            editor.putBoolean("isFirst", false);
            editor.apply();

            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //애니메이션처리
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_result);

        //안꺼지게
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mainText = findViewById(R.id.mainText);
        resultImage = findViewById(R.id.resultImage);

        Intent intent = getIntent();
        if (intent == null) {
            Log.d("MainActivity", "nulltext");
        }
        scaleInfo = intent.getStringExtra("scaleInfo");
        lowScaleInfo = intent.getStringExtra("lowScaleInfo");

        choiseImage(lowScaleInfo, scaleInfo);

        handler = new Handler();

        handler.postDelayed(runnable, 3000); //3초 지연
    }

    //이미지를 선택하는 함수
    public void choiseImage(String lowScaleInfo, String scaleInfo) {
        //경우의 수가 많으므로 switch문으로 한다.
        switch(scaleInfo) {
            case "2\' 도":
            case "2\' 레":
            case "2\' 미":
            case "2\' 파":
            case "2\' 솔":
            case "2\' 라":
            case "2\' 시":
                resultImage.setImageResource(R.drawable.cow_image);
                mainText.setText("당신의 음역대는 " + lowScaleInfo + "~" + scaleInfo + "\n묵직한 소와 같아!");
                break;
            case "3\' 도":
            case "3\' 레":
            case "3\' 미":
            case "3\' 파":
            case "3\' 솔":
            case "3\' 라":
            case "3\' 시":
                resultImage.setImageResource(R.drawable.bird_image);
                mainText.setText("당신의 음역대는 " + lowScaleInfo + "~" + scaleInfo + "\n꾀꼬리같은 음역대를 가졌군!");
                break;
            case "4\' 도":
            case "4\' 레":
            case "4\' 미":
            case "4\' 파":
            case "4\' 솔":
            case "4\' 라":
            case "4\' 시":
                resultImage.setImageResource(R.drawable.dolphin_image);
                mainText.setText("당신의 음역대는 " + lowScaleInfo + "~" + scaleInfo + "\n돌고래와 대화가 가능하겠어");
                break;
            default:
                resultImage.setImageResource(R.drawable.cow_image);
                mainText.setText("당신의 음역대는 " + lowScaleInfo + "~" + scaleInfo + "\n묵직한 소와 같아!");
                break;
        }
    }
}
