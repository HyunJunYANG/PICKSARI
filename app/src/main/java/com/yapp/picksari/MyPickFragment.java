package com.yapp.picksari;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.yapp.picksari.Adapter.MyCursorAdapter;
import com.yapp.picksari.DBHelper.DBhelper;
import com.yapp.picksari.Music.Music_list;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyPickFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyPickFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPickFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = MyPickFragment.class.getSimpleName();

    private int position;

    private MyPickFragment.OnFragmentInteractionListener mListener;

    public static ArrayList<Music_list> musicPickList;
    static DBhelper pickhelper;
    static SQLiteDatabase pickdb;
    static Cursor pickcursor;
    static MyCursorAdapter pickadapter;
    ListView lvMusicpick;

    Activity act;


    // 새로
    ListView listView;
    public static MyCursorAdapter myAdapter;
    public static DBhelper myHelper;

    Button dance;
    Button rnb;
    Button ballad;
    Button hiphop;
    Button rock;

    static String flag = "R&B";
    ImageButton btnPick;

    public static AlertDialog.Builder builder;



    public static MyPickFragment newInstance(int position) {
        MyPickFragment f = new MyPickFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }


    public MyPickFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPickFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPickFragment newInstance(String param1, String param2) {
        MyPickFragment fragment = new MyPickFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        act = getActivity();

        final View view = inflater.inflate(R.layout.fragment_my_pick, container, false);

        //플로팅액션버튼
        FloatingActionMenu menu = (FloatingActionMenu)view.findViewById(R.id.menu);
        FloatingActionButton menu1 = (FloatingActionButton)view.findViewById(R.id.menu_item) ;
        FloatingActionButton menu2 = (FloatingActionButton)view.findViewById(R.id.menu_item2);
        menu.bringToFront();

        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PitchDetectStart.class);
                startActivity(intent);
            }
        });

        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MusicInesertActivity.class);
                startActivity(intent);
            }
        });

        dance = (Button) view.findViewById(R.id.btn_dance_pick);
        ballad = (Button) view.findViewById(R.id.btn_ballad_pick);
        rnb = (Button) view.findViewById(R.id.btn_rnb_pick);
        dance = (Button) view.findViewById(R.id.btn_dance_pick);
        hiphop = (Button) view.findViewById(R.id.btn_hiphop_pick);
        rock = (Button) view.findViewById(R.id.btn_rock_pick);


        listView = (ListView) view.findViewById(R.id.lv_pick_music);
        myAdapter = new MyCursorAdapter(this.getActivity(), null);
        myHelper = new DBhelper(this.getActivity());

        ballad.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                flag = "발라드";
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                onResume();
            }
        });

        dance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                flag = "댄스";
                dance.setBackgroundResource(R.drawable.my_genre_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                onResume();
            }
        });

        hiphop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                flag = "힙합";
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                onResume();
            }
        });

        rock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                flag = "락";
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_btn);
                onResume();
            }
        });

        rnb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                flag = "R&B";
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                onResume();
            }
        });

        return view;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public void onActivityCreated(Bundle b){
        super.onActivityCreated(b);

        lvMusicpick = getActivity().findViewById(R.id.lv_pick_music);

        pickhelper = new DBhelper(getActivity());

        musicPickList = new ArrayList<Music_list>();

        pickadapter =  new MyCursorAdapter(getActivity(), null);
        lvMusicpick.setAdapter(pickadapter);


        lvMusicpick.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                final long realId = id;

            } });


    }


    public static void get_reset() {
        pickdb = pickhelper.getReadableDatabase();
        pickcursor = pickdb.rawQuery("select * from " + pickhelper.TABLE_NAME +" where mGenre = '" + flag +"';" , null);

        //커스텀 어댑터
        pickadapter.changeCursor(pickcursor);

    }
    @Override
    public void onResume() {
        super.onResume();
//        DB에서 데이터를 읽어와 Adapter에 설정
        pickdb = pickhelper.getReadableDatabase();
        pickcursor = pickdb.rawQuery("select * from " + pickhelper.TABLE_NAME +" where mGenre = '" + flag +"';" , null);

        //커스텀 어댑터
        pickadapter.changeCursor(pickcursor);

//        pickhelper.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (pickcursor != null) pickcursor.close();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




}
