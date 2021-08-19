package com.pyrion.poison_frog.egg;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.trade.AdapterRecyclerViewTrade;

import java.util.ArrayList;

public class FragmentEgg extends Fragment {

    TextView textViewMoney;
    ImageView frogBook, eggSrc;
    RecyclerView eggRecyclerView;

    AdapterRecyclerViewEgg adapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_egg_page, container, false);
        textViewMoney = view.findViewById(R.id.tv_user_money);
        frogBook = view.findViewById(R.id.frog_book);
        eggSrc = view.findViewById(R.id.egg_src);
        eggRecyclerView = view.findViewById(R.id.egg_recyclerview);

        ArrayList<String[]> eggItemArrayList= new ArrayList<>();
        String[] redEgg = getResources().getStringArray(R.array.red_box);
        String[] blueEgg = getResources().getStringArray(R.array.blue_box);
        String[] goldEgg = getResources().getStringArray(R.array.gold_box);
        eggItemArrayList.add(redEgg);
        eggItemArrayList.add(blueEgg);
        eggItemArrayList.add(goldEgg);

        //TODO 여기서 부터
        adapter = new AdapterRecyclerViewEgg(getActivity(), eggItemArrayList);
        eggRecyclerView.setAdapter(adapter);


        return  view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
