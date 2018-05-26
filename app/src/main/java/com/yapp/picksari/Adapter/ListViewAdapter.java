package com.yapp.picksari.Adapter;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yapp.picksari.Adapter.MyCursorAdapter;
import com.yapp.picksari.DBHelper.DBhelper;
import com.yapp.picksari.HomeFragment;
import com.yapp.picksari.Item.musicItem;
import com.yapp.picksari.MainActivity;
import com.yapp.picksari.MyPickFragment;
import com.yapp.picksari.R;

import java.util.List;

import static com.yapp.picksari.Adapter.MyCursorAdapter.musicdb;

/**
 * Created by myeong on 2018. 4. 2..
 */

public class ListViewAdapter extends ArrayAdapter<musicItem> {

    public static List<musicItem> items;
    static ImageButton btnPick;
    int realPosition = 0;
    static SQLiteDatabase musicdb;
    static DBhelper musichelper;

    public ListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListViewAdapter(Context context, int resource, List<musicItem> items) {
        super(context, resource, items);
        this.items = items;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        try {
            final musicItem music = items.get(position);
            if (view == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.activity_listview, null);
            }

            realPosition = position;

            Button genre = (Button) view.findViewById(R.id.tvGenre);
            Button octave = (Button) view.findViewById(R.id.tvOctave);
            TextView title = (TextView) view.findViewById(R.id.tvTitle);
            TextView singer = (TextView) view.findViewById(R.id.tvSinger);
            genre.setText(music.mGenre);
            octave.setText(music.mOctave);
            title.setText(music.mName);
            singer.setText(music.mSinger);

            final SQLiteDatabase musicdb;
            final DBhelper musichelper;
            musichelper = MyPickFragment.myHelper;
            musicdb = musichelper.getReadableDatabase();

            btnPick = (ImageButton) view.findViewById(R.id.btnPick);


            // 시작 시 노란 하트로 보이게 하려고 디비에서 불러옴
            Cursor cursor = musicdb.rawQuery("SELECT mName, mSinger FROM " + musichelper.TABLE_NAME + ";", null);

            while (cursor.moveToNext()) {
                if (cursor.getString(0).equals(music.mName) && cursor.getString(1).equals(music.mSinger)) {
                    btnPick.setImageResource(R.drawable.ic_heart_symbol_yellow);
                    music.mPick = 1;
                }
            }

            // 하트 누르자마자 리스트뷰 바로 갱신하기 위해서
            if (music.mPick == 1) {
                btnPick.setImageResource(R.drawable.ic_heart_symbol_yellow);
            }
            if (music.mPick == 0) {
                btnPick.setImageResource(R.drawable.ic_search_heart_lightgray);
            }


            btnPick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 하트 클릭시 db에 추가
                    if (music.mPick == 0) {
                        btnPick.setImageAlpha(R.drawable.ic_heart_symbol_yellow);

                        ContentValues row = new ContentValues();
                        row.put("mName", music.mName);
                        row.put("mSinger", music.mSinger);
                        row.put("mGenre", music.mGenre);
                        row.put("mOctave", music.mOctave);
                        row.put("mPick", getPosition(music)); // 홈 리스트의 실제 위치 정보 저장

                        musicdb.insert(musichelper.TABLE_NAME, null, row);
                        MyPickFragment.myAdapter.notifyDataSetChanged();
                        try {
                            HomeFragment.get_reset();
                        } catch (Exception e) {
                        }
                        try {
                            MyPickFragment.get_reset();
                        } catch (Exception e) {
                        }

                        music.mPick = 1;
                    } else if (music.mPick == 1) {
                        // 하트 클릭시 db에서 삭제
                        btnPick.setImageAlpha(R.drawable.ic_search_heart_lightgray);

                        musicdb.execSQL("DELETE FROM " + musichelper.TABLE_NAME + " WHERE mName = '" + music.mName + "' AND mSinger = '" + music.mSinger + "';");
                        MyPickFragment.myAdapter.notifyDataSetChanged();

                        try {
                            HomeFragment.get_reset();
                        } catch (Exception e) {
                        }
                        try {
                            MyPickFragment.get_reset();
                        } catch (Exception e) {
                        }

                        music.mPick = 0;
                    }
                    notifyDataSetChanged();
                }
            });
        }
        catch (Exception e) {
        }
            return view;


    }
}