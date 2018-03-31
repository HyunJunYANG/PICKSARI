package com.yapp.picksari;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;

public class MainActivity extends AppCompatActivity {


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


