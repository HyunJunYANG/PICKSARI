package com.yapp.picksari;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.yapp.picksari.Adapter.ListViewAdapter;
import com.yapp.picksari.Item.musicItem;
import com.yapp.picksari.Music.Music_list;
import com.yapp.picksari.Network.SendPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MusicInesertActivity extends AppCompatActivity{

    Spinner spinner1;
    Spinner spinner2;
    Integer[] sp1 = {1,2,3};
    String[] sp2 = {"도","레","미","파","솔","라","시"};
    private Button rnb, ballad, dance, rock, hiphop, send;
    EditText et_song, et_singer;
    SendPost sp;
    String octave,octave2,genre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_inesert);

        send = (Button)findViewById(R.id.bt_send);

        et_song = (EditText)findViewById(R.id.et_song);
        et_singer = (EditText)findViewById(R.id.et_singer);

        dance = (Button) findViewById(R.id.btn_dance);
        ballad = (Button) findViewById(R.id.btn_ballad);
        rnb = (Button) findViewById(R.id.btn_rnb);
        hiphop = (Button) findViewById(R.id.btn_hiphop);
        rock = (Button) findViewById(R.id.btn_rock);

        spinner1 = (Spinner)findViewById(R.id.spinner1);
        spinner2 = (Spinner)findViewById(R.id.spinner2);

        final Intent intent = new Intent(this, MainActivity.class);

        //최고음 spinner
        final ArrayAdapter<Integer> integerArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, sp1);
        integerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(integerArrayAdapter);
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sp2);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(stringArrayAdapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                octave = sp1[position].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                octave2 = sp2[position].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //장르 button
        dance.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                dance.setBackgroundResource(R.drawable.my_genre_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                genre = "댄스";
                onResume();
            }

        });

        ballad.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ballad.setBackgroundResource(R.drawable.my_genre_btn);
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                genre = "발라드";
                onResume();
            }

        });

        rnb.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                rnb.setBackgroundResource(R.drawable.my_genre_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                genre = "R&B";
                onResume();
            }

        });

        hiphop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiphop.setBackgroundResource(R.drawable.my_genre_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                genre = "힙합";
                onResume();
            }

        });

        rock.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                rock.setBackgroundResource(R.drawable.my_genre_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                genre = "락";
                onResume();
            }

        });

        //완료버튼 클릭시
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_singer.getText().toString().length()==0 || et_song.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(), "노래 정보가 입력되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
                else {
                    String octave_all = octave + "\' " + octave2;
                    JSONObject jsonParam = new JSONObject();
                    try {
                        jsonParam.put("mName", et_song.getText().toString());
                        jsonParam.put("mSinger", et_singer.getText().toString());
                        jsonParam.put("mOctave", octave_all);
                        jsonParam.put("mGenre", genre);
                        jsonParam.put("mPick", 0);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    sp = new SendPost(jsonParam, "/register_music");
                    new Thread() {
                        public void run() {
                            sp.executeClient();
                        }
                    }.start();
                    Toast.makeText(getApplicationContext(), "노래 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    HomeFragment.myAdapter.notifyDataSetChanged();
                    finish();
                    startActivity(intent);
                }
            }
        });
    }
}