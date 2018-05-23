package com.yapp.picksari;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.yapp.picksari.Item.musicItem;
import com.yapp.picksari.Network.SendPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SendPost octave_listSp;
    private Handler handler;
    private Bundle bundle;
    private Fragment f;
    public List<musicItem> list = new ArrayList<>();
    public List<musicItem> dance_list = new ArrayList<>();
    public List<musicItem> ballad_list = new ArrayList<>();
    public List<musicItem> rnb_list = new ArrayList<>();
    public List<musicItem> hiphop_list = new ArrayList<>();
    public List<musicItem> rock_list = new ArrayList<>();


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
        EditText etSearch = (EditText)findViewById(R.id.et_search);
        ivSearch.bringToFront();
        ImageView icon = (ImageView)findViewById(R.id.logo_image);
        icon.bringToFront();

        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);


        //검색창이나 검색버튼(돋보기모양) 누르면 SearchActivity로 전환
        final Intent searchintent = new Intent(this, SearchActivity.class);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(searchintent);
            }
        });
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(searchintent);
            }
        });

        //안꺼지게
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TextView mainText = findViewById(R.id.mainText);

        /*final Intent intent = getIntent();
        if(intent == null) {
            Log.d("MainActivity", "nulltext");
        }
        String scaleInfo = intent.getStringExtra("scaleInfo");
        String scaleInfotwo = intent.getStringExtra("scaleInfotwo");*/

        //intent로 받으면 다시 측정했을 때 scaleinfo랑 scaleinfotwo값을 못불러와서 SP로 함
        SharedPreferences sharedpreferences = getSharedPreferences("Pref", MODE_PRIVATE);
        final String scaleInfo = sharedpreferences.getString("scaleInfo","null");
        final String scaleInfotwo = sharedpreferences.getString("scaleInfotwo","null");

        TextView egOctave_min = findViewById(R.id.egOctave_min);
        TextView egOctave_max = findViewById(R.id.egOctave_max);
        TextView krOctave_min = findViewById(R.id.krOctave_min);
        TextView krOctave_max = findViewById(R.id.krOctave_max);

//        egOctave_max.setText(scaleInfo);
//        egOctave_min.setText(scaleInfotwo);

        // 최고음 번역
        if (scaleInfo.substring(3,4).equals("도")) {
            egOctave_max.setText(scaleInfo.charAt(0) + "'C");
        }
        else if (scaleInfo.substring(3,4).equals("레")) {
            egOctave_max.setText(scaleInfo.charAt(0) + "'D");
        }
        else if (scaleInfo.substring(3,4).equals("미")) {
            egOctave_max.setText(scaleInfo.charAt(0) + "'E");
        }
        else if (scaleInfo.substring(3,4).equals("파")) {
            egOctave_max.setText(scaleInfo.charAt(0) + "'F");
        }
        else if (scaleInfo.substring(3,4).equals("솔")) {
            egOctave_max.setText(scaleInfo.charAt(0) + "'G");
        }
        else if (scaleInfo.substring(3,4).equals("라")) {
            egOctave_max.setText(scaleInfo.charAt(0) + "'A");
        }
        else if (scaleInfo.substring(3,4).equals("시")) {
            egOctave_max.setText(scaleInfo.charAt(0) + "'B");
        }

        // 최저음 번역
        if (scaleInfotwo.substring(3,4).equals("도")) {
            egOctave_min.setText(scaleInfotwo.charAt(0) + "'C");
        }
        else if (scaleInfotwo.substring(3,4).equals("레")) {
            egOctave_min.setText(scaleInfotwo.charAt(0) + "'D");
        }
        else if (scaleInfotwo.substring(3,4).equals("미")) {
            egOctave_min.setText(scaleInfotwo.charAt(0) + "'E");
        }
        else if (scaleInfotwo.substring(3,4).equals("파")) {
            egOctave_min.setText(scaleInfotwo.charAt(0) + "'F");
        }
        else if (scaleInfotwo.substring(3,4).equals("솔")) {
            egOctave_min.setText(scaleInfotwo.charAt(0) + "'G");
        }
        else if (scaleInfotwo.substring(3,4).equals("라")) {
            egOctave_min.setText(scaleInfotwo.charAt(0) + "'A");
        }
        else if (scaleInfotwo.substring(3,4).equals("시")) {
            egOctave_min.setText(scaleInfotwo.charAt(0) + "'B");
        }

        krOctave_max.setText(scaleInfo.charAt(0) + " 옥타브 " + scaleInfo.substring(3,4));
        krOctave_min.setText(scaleInfotwo.charAt(0) + " 옥타브 " + scaleInfotwo.substring(3,4));


        handler = new Handler();
        bundle = new Bundle();
        f = new Fragment();
        String mOctave = scaleInfo;

        JSONObject jsonParam = new JSONObject();
        try {
//            jsonParam.put("mOctave", scaleInfo.toString());
            jsonParam.put("mOctave", mOctave);
//            jsonParam.put("mGenre", mGenre.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        octave_listSp = new SendPost(jsonParam, "/octave_list");
        final String[] SPresult = new String[1];
        new Thread() {
            public void run() {
                SPresult[0] = octave_listSp.executeClient();
                System.out.println(SPresult);
                handler.post(new Runnable() {
                    public void run() {
                        JSONParser(SPresult);
                        bundle.putParcelableArrayList("rnblist", (ArrayList<? extends Parcelable>) rnb_list);
                        bundle.putParcelableArrayList("dancelist", (ArrayList<? extends Parcelable>) dance_list);
                        bundle.putParcelableArrayList("balladlist", (ArrayList<? extends Parcelable>) ballad_list);
                        bundle.putParcelableArrayList("hiphop", (ArrayList<? extends Parcelable>) hiphop_list);
                        bundle.putParcelableArrayList("rocklist", (ArrayList<? extends Parcelable>) rock_list);
                        pager.setAdapter(new MyAdapter(getSupportFragmentManager()));
                        f.setArguments(bundle);
                        tabs.setViewPager(pager);
                    }
                });
            }
        }.start();


    }

    void JSONParser(String[] SP) {
        try {
            JSONArray JArray = new JSONArray(SP[0].toString());   // JSONArray 생성
            for (int i = 0; i < JArray.length(); i++) {
                JSONObject jk = JArray.getJSONObject(i);  // JSONObject 추출

                String mName = jk.getString("mName");
                String mSinger = jk.getString("mSinger");
                String mOctave = jk.getString("mOctave");
                String mGenre = jk.getString("mGenre");

                if(mGenre.equals("댄스"))
                    dance_list.add(new musicItem(mName, mSinger,mOctave, mGenre, 0));
                else if(mGenre.equals("발라드"))
                    ballad_list.add(new musicItem(mName, mSinger,mOctave, mGenre, 0));
                else if(mGenre.equals("R&B"))
                    rnb_list.add(new musicItem(mName, mSinger,mOctave, mGenre, 0));
                else if(mGenre.equals("힙합"))
                    hiphop_list.add(new musicItem(mName, mSinger,mOctave, mGenre, 0));
                else if(mGenre.equals("락"))
                    rock_list.add(new musicItem(mName, mSinger,mOctave, mGenre, 0));
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
        private String[] titles = { getString(R.string.main_home), getString(R.string.main_mypick) };

        public MyAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch(position){
                case 0:{
                    return HomeFragment.newInstance(position, list, dance_list, ballad_list, hiphop_list, rock_list);
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


