package com.pyrion.poison_frog.trade;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.OneFrogSet;

import java.util.ArrayList;

public class FragmentTrade extends Fragment {
    ArrayList<OneFrogSet> oneFrogSetList = new ArrayList<>();
    Cursor cursor_frog, cursor_user;
    SQLiteDatabase database_frog;
    SQLiteDatabase database_user;

    AdapterRecyclerViewTrade adapter;

    View view;
    ImageView tradeCenterFrog;
    TextView woodNoticeText;
    RecyclerView tradeFrogRecyclerView;

    int selectedFrogKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateFrogListDB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trade_page, container, false);

        tradeCenterFrog = view.findViewById(R.id.trade_frog_center_src);
        tradeFrogRecyclerView = view.findViewById(R.id.trade_frog_recyclerview);
        woodNoticeText = view.findViewById(R.id.notice_text);

        setSelectedViews();

        adapter = new AdapterRecyclerViewTrade(getActivity(), oneFrogSetList, view);
        tradeFrogRecyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateFrogListDB();
        setSelectedViews();
        
        adapter = new AdapterRecyclerViewTrade(getActivity(), oneFrogSetList, view);
        tradeFrogRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        updateFrogListDB();
        setSelectedViews();
        return super.onContextItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //lisview 클릭된거 대응하기
    }
    
    void updateFrogListDB(){
        oneFrogSetList.clear();

        database_user = getActivity().openOrCreateDatabase("userDB.db", getActivity().MODE_PRIVATE, null);
        cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        cursor_user.moveToNext();
        selectedFrogKey = cursor_user.getInt(cursor_user.getColumnIndex("selected_frog_key"));

        database_frog = getActivity().openOrCreateDatabase("frogsDB.db", getActivity().MODE_PRIVATE, null);
        cursor_frog= database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
        if(cursor_frog!=null) {
            while (cursor_frog.moveToNext()) {//[레코드:row]로 커서이동
                int frog_key = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_key"));
                int house_type = cursor_frog.getInt(cursor_frog.getColumnIndex("house_type"));
                String creator_name = cursor_frog.getString(cursor_frog.getColumnIndex("creator_name"));
                String frog_name = cursor_frog.getString(cursor_frog.getColumnIndex("frog_name"));
                int frog_state = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_state"));
                int frog_species = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_species"));
                int frog_size = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_size"));
                int frog_power = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_power"));

                if(frog_key == selectedFrogKey) {
                    oneFrogSetList.add(0, new OneFrogSet(
                            frog_key,
                            house_type,
                            creator_name,
                            frog_name,
                            frog_state,
                            frog_species,
                            frog_size,
                            frog_power));
                    Log.i("hyunju", frog_key+"");
                    continue;
                }
                oneFrogSetList.add(new OneFrogSet(
                        frog_key,
                        house_type,
                        creator_name,
                        frog_name,
                        frog_state,
                        frog_species,
                        frog_size,
                        frog_power));
                Log.i("hyunju", frog_key+"");
            }

        }
        //add buy new house event
        oneFrogSetList.add(new OneFrogSet());
    }
    
    
    void setSelectedViews(){
        tradeCenterFrog.setImageResource( oneFrogSetList.get(0).getFrogSrc());
        Log.i("trade", oneFrogSetList.get(0).getHouseType()+"");
        if(Frog.STATE_SOLD == oneFrogSetList.get(0).getFrogState()){
            woodNoticeText.setText("근처에 개구리가 나타나면 터치해서 잡으세요.");
        }else{
            woodNoticeText.setText("개구리를 꾹 누르면 근처 사람에게 공유됩니다.");
        }
    }


}
