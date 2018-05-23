package com.yapp.picksari;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yapp.picksari.Adapter.ListViewAdapter;
import com.yapp.picksari.Adapter.SearchAdapter;
import com.yapp.picksari.Item.musicItem;
import com.yapp.picksari.Network.SendPost;
import com.yapp.picksari.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SendPost octave_listSp;
    private Handler handler;
    private Bundle bundle;
    public List<musicItem> all_list = new ArrayList<>();
    public List<musicItem> search_list_low = new ArrayList<>();
    public List<musicItem> search_list_high = new ArrayList<>();
    public List<String> search_list = new ArrayList<>();
    List<String> real_search_list;
    private SearchAdapter searchAdapter;
    private ListView searchView;
    LinearLayout resultView;
    private ArrayList<String> arraylist = new ArrayList<>();

    private static final int PERMISSIONS_REQUEST_RECORD = 100;

    ListView listView_low,listview_high;
    ListViewAdapter lowAdapter,highAdapter;

    String[] OctArray = {"도","레","미","파","솔","라","시"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText et_search = (EditText)findViewById(R.id.et_search);
        final ImageButton search_button = (ImageButton)findViewById(R.id.search_button);
        searchView = (ListView) findViewById(R.id.searchView);
        resultView = (LinearLayout) findViewById(R.id.resultView);

        SharedPreferences sharedpreferences = getSharedPreferences("Pref", MODE_PRIVATE);
        final String myOct = sharedpreferences.getString("scaleInfo","null");

        //안꺼지게
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        handler = new Handler();
        bundle = new Bundle();
        String mOctave_all = "3\' 시";

        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("mOctave", mOctave_all);
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
                        real_search_list = new ArrayList<>(new HashSet<String>(search_list));
                        arraylist.addAll(real_search_list);
                        searchAdapter = new SearchAdapter(real_search_list,getApplicationContext());
                        searchView.setAdapter(searchAdapter);
                        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
                        et_search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                // input창에 문자를 입력할때마다 호출된다.
                                // search 메소드를 호출한다.
                                final String text = et_search.getText().toString();
                                realtime_search(text);
                                search_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        search(text);
                                        resultView.setVisibility(View.VISIBLE);
                                        searchView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });

                        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                et_search.setText(real_search_list.get(position).toString());
                                search(et_search.getText().toString());
                                resultView.setVisibility(View.VISIBLE);
                                searchView.setVisibility(View.GONE);
                            }
                        });

                        et_search.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                searchView.setVisibility(View.VISIBLE);
                                resultView.setVisibility(View.GONE);
                            }
                        });
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

                all_list.add(new musicItem(mName,mSinger,mOctave, mGenre, 0));
                search_list.add(mName);
                search_list.add(mSinger);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void realtime_search(String charText) {
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        real_search_list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            real_search_list.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < arraylist.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist.get(i).toLowerCase().contains(charText))
                {
                    real_search_list.add(arraylist.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        searchAdapter.notifyDataSetChanged();
    }

    //검색기능
    public void search(String text){
        search_list_low.clear();
        search_list_high.clear();
        String searchText = text;
        if(searchText.equals(""))
            Toast.makeText(getApplicationContext(),"검색어를 입력해주세요.",Toast.LENGTH_SHORT).show();
        else {
            for(int i=0;i<all_list.size();i++){
                //노래 제목이나 가수 이름에 searchText가 들어가있을 경우
                if(all_list.get(i).mName.contains(searchText) || all_list.get(i).mSinger.contains(searchText)){
                                            /*//searchText를 포함하는 노래가 내 옥타브보다 낮거나 같으면
                                            if(myOct.charAt(0) > all_list.get(i).mOctave.charAt(0)){
                                                search_list_low.add(all_list.get(i));
                                            }
                                            else if (myOct.charAt(0) == all_list.get(i).mOctave.charAt(0)) {
                                                    int my_Oct = 0, search_Oct = 0;
                                                    for (int j = 0; j < OctArray.length; j++) {
                                                        if (OctArray[j].equals(myOct.substring(3, 4)))
                                                            my_Oct = j;
                                                        if (OctArray[j].equals(all_list.get(i).mOctave.substring(3, 4)))
                                                            search_Oct = j;
                                                    }
                                                    if (my_Oct >= search_Oct)
                                                        search_list_low.add(all_list.get(i));
                                                    else
                                                        search_list_high.add(all_list.get(i));
                                                }
                                                else {
                                                //searchText를 포함하는 노래가 내 옥타브보다 높으면
                                                search_list_high.add(all_list.get(i));
                                            }*/
                    search_list_low.add(all_list.get(i));
                }
            }
            if(search_list_high.isEmpty()&&search_list_low.isEmpty())
                Toast.makeText(getApplicationContext(),"검색어가 존재하지 않습니다.",Toast.LENGTH_SHORT).show();

            listView_low = (ListView)findViewById(R.id.lv_lowOct_music);
            lowAdapter = new ListViewAdapter(getApplicationContext(), android.R.layout.activity_list_item,search_list_low);
            listView_low.setAdapter(lowAdapter);
//                                    listview_high = (ListView)findViewById(R.id.lv_highOct_music);
//                                    highAdapter = new ListViewAdapter(getApplicationContext(), android.R.layout.activity_list_item,search_list_high);
//                                    listview_high.setAdapter(highAdapter);
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

}
