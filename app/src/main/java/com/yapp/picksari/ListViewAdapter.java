package com.yapp.picksari;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yapp.picksari.Item.musicItem;

import java.util.List;

/**
 * Created by myeong on 2018. 4. 2..
 */

public class ListViewAdapter extends ArrayAdapter<musicItem>{

    private List<musicItem> items;

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

        musicItem music = items.get(position);
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


        return view;
    }
}
