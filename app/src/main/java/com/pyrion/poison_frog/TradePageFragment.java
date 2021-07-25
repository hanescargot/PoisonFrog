package com.pyrion.poison_frog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

public class TradePageFragment extends Fragment {

    ImageView tradeFrog;
    ListView tradeFrogListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        tradeFrogListView  items 더하기

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_trade_page, container, false);
        return returnView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tradeFrog=view.findViewById(R.id.trade_frog);
        tradeFrogListView=view.findViewById(R.id.trade_frog_listview);
        //lisview 클릭된거 대응하기
    }
}
