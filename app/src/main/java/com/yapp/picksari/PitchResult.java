package com.yapp.picksari;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PitchResult extends AppCompatActivity {

    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    ImageView resultImage;
    Handler handler;
    TextView mainText;
    String scaleInfo;
    String lowScaleInfo;
    ImageView share;
    View container;
    Bitmap captureView;
    String address;

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
        share = findViewById(R.id.share);

        Intent intent = getIntent();
        if (intent == null) {
            Log.d("MainActivity", "nulltext");
        }
        scaleInfo = intent.getStringExtra("scaleInfo");
        lowScaleInfo = intent.getStringExtra("lowScaleInfo");

        choiseImage(lowScaleInfo, scaleInfo);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //권한 설정
                if (ContextCompat.checkSelfPermission(PitchResult.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PitchResult.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast toast = Toast.makeText(getApplicationContext(),"음역대를\n공유하기 위한 권한입니다.", Toast.LENGTH_LONG);
                        ViewGroup viewGroup = (ViewGroup)toast.getView();
                        TextView textView = (TextView)viewGroup.getChildAt(0);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        toast.show();
                        ActivityCompat.requestPermissions(PitchResult.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                    } else {
                        ActivityCompat.requestPermissions(PitchResult.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                    }
                } else {
                    screenShot();
                    share();
                }
            }
        });

        /*
        handler = new Handler();

        handler.postDelayed(runnable, 3000); //3초 지연
        */
    }

    public void screenShot() {
        container = getWindow().getDecorView();
        container.buildDrawingCache();
        captureView = container.getDrawingCache();

        address = Environment.getExternalStorageDirectory().toString() + "/pitchResult.jpeg";
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(address);
            captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }catch (FileNotFoundException e) {
                e.printStackTrace();
        }
    }

    public void share() {
        Uri uri = Uri.fromFile(new File(address));
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent,"나의 음역대"));
    }

    //권한 설정에서 아니요를 누른 경우
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        screenShot();
                        share();
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                    Toast toast = Toast.makeText(getApplicationContext(), "Storage 사용을 거부하여\n음역대를 공유할 수 없습니다.", Toast.LENGTH_LONG);
                    ViewGroup viewGroup = (ViewGroup) toast.getView();
                    TextView textView = (TextView) viewGroup.getChildAt(0);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    toast.show();
                }
                return;
        }
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
                resultImage.setImageResource(R.drawable.cow_image);
                mainText.setText("당신의 음역대는 " + lowScaleInfo + "~" + scaleInfo + "\n묵직한 소와 같아!");
                break;
            case "2\' 라":
            case "2\' 시":
            case "3\' 도":
            case "3\' 레":
                resultImage.setImageResource(R.drawable.bird_image);
                mainText.setText("당신의 음역대는 " + lowScaleInfo + "~" + scaleInfo + "\n꾀꼬리같은 음역대를 가졌군!");
                break;
            case "3\' 미":
            case "3\' 파":
            case "3\' 솔":
            case "3\' 라":
            case "3\' 시":
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

    public void mOnClick(View view) {
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
    }
}
