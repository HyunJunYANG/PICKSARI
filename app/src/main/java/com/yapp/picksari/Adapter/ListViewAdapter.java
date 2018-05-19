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

/**
 * Created by myeong on 2018. 4. 2..
 */

public class ListViewAdapter extends ArrayAdapter<musicItem>{

    private List<musicItem> items;
    ImageButton btnPick;
    int flag = -1;

    public ListViewAdapter(Context context, int textViewResourceId) {
        super(context,textViewResourceId);
    }

    public ListViewAdapter(Context context, int resource, List<musicItem> items) {
        super(context, resource, items);
        this.items = items;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        final musicItem music = items.get(position);
        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.activity_listview, null);
        }

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

        btnPick = (ImageButton)view.findViewById(R.id.btnPick);

        Cursor cursor = musicdb.rawQuery("SELECT _id FROM " + musichelper.TABLE_NAME + " WHERE mName='" +  music.mName + "' AND mSinger='" + music.mSinger + "';", null);

        while (cursor.moveToNext()) {
            if (cursor.getInt(0) != -1) {
                btnPick.setImageResource(R.drawable.ic_heart_symbol_yellow);
                flag = cursor.getInt(0);
            }
        }

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 하트 클릭시 db에 추가

                if (flag == -1) {
                    btnPick.setImageResource(R.drawable.ic_heart_symbol_yellow);

                    ContentValues row = new ContentValues();
                    row.put("mName", music.mName);
                    row.put("mSinger", music.mSinger);
                    row.put("mGenre", music.mGenre);
                    row.put("mOctave", music.mOctave);
                    row.put("mPick", 1);

                    musicdb.insert(musichelper.TABLE_NAME, null, row);
                    MyPickFragment.myAdapter.notifyDataSetChanged();

                    MyPickFragment.get_reset();

                    // 바로 바뀌어야 함



                }
                else {
                    btnPick.setImageAlpha(R.drawable.ic_search_heart_lightgray);

                    // 하트 클릭시 db에서 삭제
                    musicdb.execSQL("DELETE FROM " + musichelper.TABLE_NAME + " WHERE _id = " + flag + ";");
                    MyPickFragment.myAdapter.notifyDataSetChanged();

                    MyPickFragment.get_reset();

                    // 바뀌어야 함
                }
            }
        });


        return view;
    }
}