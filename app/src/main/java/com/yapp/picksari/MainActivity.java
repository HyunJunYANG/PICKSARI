package com.yapp.picksari;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.yapp.picksari.Music.Music_list;
import com.yapp.picksari.Network.SendPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SendPost octave_listSp;
    private Handler handler;
    public ArrayList<Music_list> items= new ArrayList<>();

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_RECORD = 100;
    static int cnt = 0;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView ivSearch = (ImageView)findViewById(R.id.iv_search);

        ivSearch.bringToFront();

        ImageView icon = (ImageView)findViewById(R.id.logo_image);

        icon.bringToFront();


        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

//안꺼지게
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TextView mainText = findViewById(R.id.mainText);

        Intent intent = getIntent();
        if(intent == null) {
            Log.d("MainActivity", "nulltext");
        }
        String scaleInfo = intent.getStringExtra("scaleInfo");

//        mainText.setText(scaleInfo);

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
//                        System.out.println(items.get(0).getmSinger());
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ((grantResults.length == 0) || (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            return;
        }

        switch(requestCode) {
            case PERMISSIONS_REQUEST_RECORD:
                break;
        }
    }


    public class MyAdapter extends FragmentPagerAdapter {
        private String[] titles = { getString(R.string.main_home),
                getString(R.string.main_mypick) };

        public MyAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch(position){
                case 0:{
                    return HomeFragment.newInstance(position);
                }
                case 1:{
                    return MyPickFragment.newInstance(position);
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}


