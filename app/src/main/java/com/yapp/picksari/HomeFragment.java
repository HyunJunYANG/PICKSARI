package com.yapp.picksari;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.yapp.picksari.Adapter.ListViewAdapter;
import com.yapp.picksari.Adapter.MyCursorAdapter;
import com.yapp.picksari.DBHelper.DBhelper;
import com.yapp.picksari.Item.musicItem;


import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private Bundle bundle = getArguments();
    private int position;

    private OnFragmentInteractionListener mListener;

    private static List<musicItem> item;
    public static List<musicItem> dance_list, ballad_list, rnb_list, rock_list, hiphop_list;
    private Button rnb, ballad, dance, rock, hiphop;
    private int id = 1;

    // 새로
    ListView listView;
    public static ListViewAdapter myAdapter;
    public static DBhelper myHelper;

    ImageButton btnPick;

    public static HomeFragment newInstance(int position) {
        HomeFragment f = new HomeFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);

        f.setArguments(b);

        return f;
    }

    public HomeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(int param1, List<musicItem> param2, List<musicItem> param3,
                                           List<musicItem> param4, List<musicItem> param5,
                                           List<musicItem> param6) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putParcelableArrayList(ARG_PARAM2, (ArrayList<? extends Parcelable>) param2);
        args.putParcelableArrayList(ARG_PARAM3, (ArrayList<? extends Parcelable>) param3);
        args.putParcelableArrayList(ARG_PARAM4, (ArrayList<? extends Parcelable>) param4);
        args.putParcelableArrayList(ARG_PARAM5, (ArrayList<? extends Parcelable>) param5);
        args.putParcelableArrayList(ARG_PARAM6, (ArrayList<? extends Parcelable>) param6);

//        item = param2;
        rnb_list = param2;
        dance_list = param3;
        ballad_list = param4;
        hiphop_list = param5;
        rock_list = param6;


        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        dance = (Button) view.findViewById(R.id.btn_dance);
        ballad = (Button) view.findViewById(R.id.btn_ballad);
        rnb = (Button) view.findViewById(R.id.btn_rnb);
        dance = (Button) view.findViewById(R.id.btn_dance);
        hiphop = (Button) view.findViewById(R.id.btn_hiphop);
        rock = (Button) view.findViewById(R.id.btn_rock);



        myHelper = new DBhelper(this.getActivity());


        dance.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){

                listView = (ListView) view.findViewById(R.id.lv_music);
                myAdapter = new ListViewAdapter(getActivity(), android.R.layout.activity_list_item, dance_list);
                listView.setAdapter(myAdapter);

                dance.setBackgroundResource(R.drawable.my_genre_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                onResume();
            }

        });


        ballad.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView = (ListView) view.findViewById(R.id.lv_music);
                myAdapter = new ListViewAdapter(getActivity(), android.R.layout.activity_list_item, ballad_list);
                listView.setAdapter(myAdapter);

                ballad.setBackgroundResource(R.drawable.my_genre_btn);
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                onResume();
            }

        });
        rnb.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView = (ListView) view.findViewById(R.id.lv_music);
                myAdapter = new ListViewAdapter(getActivity(), android.R.layout.activity_list_item, rnb_list);
                listView.setAdapter(myAdapter);

                rnb.setBackgroundResource(R.drawable.my_genre_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                onResume();
            }

        });
        hiphop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView = (ListView) view.findViewById(R.id.lv_music);
                myAdapter = new ListViewAdapter(getActivity(), android.R.layout.activity_list_item, hiphop_list);
                listView.setAdapter(myAdapter);

                hiphop.setBackgroundResource(R.drawable.my_genre_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                rock.setBackgroundResource(R.drawable.my_genre_not_btn);
                onResume();
            }

        });
        rock.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView = (ListView) view.findViewById(R.id.lv_music);
                myAdapter = new ListViewAdapter(getActivity(), android.R.layout.activity_list_item, rock_list);
                listView.setAdapter(myAdapter);

                rock.setBackgroundResource(R.drawable.my_genre_btn);
                ballad.setBackgroundResource(R.drawable.my_genre_not_btn);
                rnb.setBackgroundResource(R.drawable.my_genre_not_btn);
                hiphop.setBackgroundResource(R.drawable.my_genre_not_btn);
                dance.setBackgroundResource(R.drawable.my_genre_not_btn);
                onResume();
            }

        });

        ListView listView = (ListView) view.findViewById(R.id.lv_music);
        listView.setAdapter(new ListViewAdapter(getActivity(), android.R.layout.activity_list_item, rnb_list));


        return view;
    }

    public static void get_reset() {
        myAdapter.notifyDataSetChanged();

    }
    @Override
    public void onResume() {
        super.onResume();


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
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


    //    public static HomeFragment newInstance(String param1, String param2) {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM1, param2);
//        fragment.setArguments(args);
//
//        return fragment;
//    }
}

