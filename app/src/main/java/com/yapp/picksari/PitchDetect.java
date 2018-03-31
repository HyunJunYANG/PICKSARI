package com.yapp.picksari;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PitchDetect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_detect);
    }

    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.btn_tomain:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
        }
    }
}
