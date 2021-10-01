package com.pyrion.game.poison_frog.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pyrion.poison_frog.R;

public class FragmentTrade extends Fragment {
    AdapterRecyclerViewTrade adapter;

    View view;
    RecyclerView tradeFrogRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trade_page, container, false);

        ImageView map = view.findViewById(R.id.map);
        ImageView camera = view.findViewById(R.id.camera);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Map
                //지도 배경에 근처 개구리 랜덤으로 보여주기
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Camera
                //1미터 이내면 개구리 보여주고 터치하면 잡을 수 있기
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tradeFrogRecyclerView = view.findViewById(R.id.trade_frog_recyclerview);
        adapter = new AdapterRecyclerViewTrade(getActivity(), view);
        tradeFrogRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
