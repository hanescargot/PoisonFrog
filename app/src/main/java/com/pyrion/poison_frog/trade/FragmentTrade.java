package com.pyrion.poison_frog.trade;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.OneFrogSet;

import java.util.ArrayList;

public class FragmentTrade extends Fragment {
    ArrayList<OneFrogSet> oneFrogSetList = new ArrayList<>();
    Cursor cursor_frog;
    SQLiteDatabase database_frog;
    SQLiteDatabase database_user;

    AdapterRecyclerViewTrade adapter;

    ImageView tradeCenterFrog;
    RecyclerView tradeFrogRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//TODO intent로 안받고 db나 static으로 받아도 될까?
        oneFrogSetList.clear();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_trade_page, container, false);

        // TODO Center frog image 실제 데이터랑 메치 시키기.
        tradeCenterFrog =returnView.findViewById(R.id.trade_frog_center_src);

        tradeFrogRecyclerView = returnView.findViewById(R.id.trade_frog_recyclerview);
        adapter = new AdapterRecyclerViewTrade(getActivity(), oneFrogSetList);
        tradeFrogRecyclerView.setAdapter(adapter);
        return returnView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //lisview 클릭된거 대응하기
    }
}
