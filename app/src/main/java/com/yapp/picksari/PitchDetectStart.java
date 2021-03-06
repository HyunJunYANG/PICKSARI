package com.yapp.picksari;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PitchDetectStart extends AppCompatActivity {

    ImageView startImage;
    static String LOG_TAG="PitchDetectStart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_detect_start);

        //안꺼지게
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startImage = findViewById(R.id.StartImage);
        TextView gpl = findViewById(R.id.gpl);

        gpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.gnu.org/licenses/gpl-3.0.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        TextView permission = findViewById(R.id.permission);

        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://blog.naver.com/yhj780/221286399335");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //터치리스너, 누르면 색이 변하고 떼면 넘어감
        startImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startImage.setImageResource(R.drawable.start_yellow);
                        break;
                    case MotionEvent.ACTION_UP:
                        startImage.setImageResource(R.drawable.start_white);
                        Intent intent = new Intent(PitchDetectStart.this, PitchDetectStart2.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });

        /*

        //이미지 클릭하면 PitchDetect로 넘어가도록
        startImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImage.setImageResource(R.drawable.start_yellow);
                Intent intent = new Intent(PitchDetectStart.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        */
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
