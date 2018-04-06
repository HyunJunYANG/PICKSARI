package com.yapp.picksari;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class PitchDetectStart2 extends AppCompatActivity {

    private Handler handler;
    static String LOG_TAG="PitchDetectStart2";
    final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    //postDealyed로 실행되는 쓰레드, PitchDetect 액티비티로 넘어간다
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(PitchDetectStart2.this, PitchDetect.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_detect_start2);

        //안꺼지게
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //권한 설정
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }

        TextView textView = findViewById(R.id.startTextView);
        textView.bringToFront();
        textView.setText("당신’의 음역대를 \n측정 해보겠소.");

        handler = new Handler();
        handler.postDelayed(runnable, 2000); //2초 지연
    }

    //첫 권한 설정에서 아니요를 누른 경우
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                }
                return;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
