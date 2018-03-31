package com.yapp.picksari;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.yapp.picksari.Music.Music_list;
import com.yapp.picksari.Network.SendPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PitchResult extends AppCompatActivity {
    private SendPost octave_listSp;
    private Handler handler;
    public ArrayList<Music_list> items= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_result);

        //안꺼지게
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TextView mainText = findViewById(R.id.mainText);

        Intent intent = getIntent();
        if(intent == null) {
            Log.d("MainActivity", "nulltext");
        }
        String scaleInfo = intent.getStringExtra("scaleInfo");

        mainText.setText(scaleInfo);

        handler = new Handler();

        String mOctave = "3\' 도";
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("mOctave", scaleInfo.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        octave_listSp = new SendPost(jsonParam, "/octave_list");

        final String[] SPresult = new String[1];
        new Thread() {
            public void run() {
                SPresult[0] = octave_listSp.executeClient();
                handler.post(new Runnable() {
                    public void run() {
                        if(SPresult != null)
                            JSONParser(SPresult);
                            System.out.println(items.get(0).getmSinger());
                    }
                });
            }
        }.start();
    }

    void JSONParser(String[] SP) {
        try {
            JSONArray JArray = new JSONArray(SP[0].toString());   // JSONArray 생성
            Music_list[] music_array = new Music_list[JArray.length()];
            for (int i = 0; i < JArray.length(); i++) {
                JSONObject jk = JArray.getJSONObject(i);  // JSONObject 추출

                music_array[i] = new Music_list();
                music_array[i].setmName(jk.getString("mName"));
                music_array[i].setmSinger(jk.getString("mSinger"));
                music_array[i].setmOctave(jk.getString("mOctave"));
                music_array[i].setmGenre(jk.getString("mGenre"));

                items.add(music_array[i]);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
