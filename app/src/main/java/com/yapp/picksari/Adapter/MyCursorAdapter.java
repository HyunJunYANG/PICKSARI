package com.yapp.picksari.Adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yapp.picksari.DBHelper.DBhelper;
import com.yapp.picksari.HomeFragment;
import com.yapp.picksari.MainActivity;
import com.yapp.picksari.Music.Music_list;
import com.yapp.picksari.MyPickFragment;
import com.yapp.picksari.R;

import java.util.ArrayList;

/**
 * Created by dain on 2017-09-24.
 */

public class MyCursorAdapter extends CursorAdapter {


    LayoutInflater inflater;
    public static Cursor cursor;
    private static int flag = 0;
    public static ImageButton btnPick;
    public static SQLiteDatabase musicdb;
    public static DBhelper musichelper;

    public MyCursorAdapter(Context context, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cursor = c;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemLayout = inflater.inflate(R.layout.activity_listview, parent, false);
        return listItemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvSinger = view.findViewById(R.id.tvSinger);
        Button tvGenre = view.findViewById(R.id.tvGenre);
        Button tvOctave = view.findViewById(R.id.tvOctave);

        tvTitle.setTextColor(Color.parseColor("#FFFFFF"));

        tvTitle.setText(cursor.getString(1));
        tvSinger.setText(cursor.getString(2));
        tvGenre.setText(cursor.getString(3));
        tvOctave.setText(cursor.getString(4));

        final int realId = cursor.getInt(0);

        musichelper = MyPickFragment.myHelper;
        musicdb = musichelper.getReadableDatabase();

        // 버튼 클릭 시 아이콘 색 바꾸고 디비에서 제거
        btnPick = (ImageButton) view.findViewById(R.id.btnPick);
        btnPick.setImageResource(R.drawable.ic_heart_symbol_yellow);

        btnPick.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                btnPick.setImageAlpha(R.drawable.ic_search_heart_lightgray);

                // 하트 클릭시 db에서 삭제
                musicdb.execSQL("DELETE FROM " + musichelper.TABLE_NAME + " WHERE _id = " + realId + ";");
                MyPickFragment.myAdapter.notifyDataSetChanged();

                MyPickFragment.get_reset();
            }
        });
    }











































































}
