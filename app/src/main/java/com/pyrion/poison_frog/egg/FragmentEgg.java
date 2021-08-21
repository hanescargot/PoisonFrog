package com.pyrion.poison_frog.egg;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pyrion.poison_frog.MainActivity;
import com.pyrion.poison_frog.R;

import java.util.ArrayList;

public class FragmentEgg extends Fragment {

    TextView textViewMoney;
    ImageView frogBook, eggSrc;
    RecyclerView eggRecyclerView;

    AdapterRecyclerViewEgg adapter;

    int userMoney = 0;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_egg_page, container, false);
        textViewMoney = view.findViewById(R.id.tv_user_money);
        frogBook = view.findViewById(R.id.frog_book);
        eggSrc = view.findViewById(R.id.egg_src);
        eggRecyclerView = view.findViewById(R.id.egg_recyclerview);

        getUserDB();
        textViewMoney.setText(userMoney+"");

        //egg item 기본 값 넣기 todo json 형식으로 저장해 두기 .
        ArrayList<String[]> eggItemArrayList= new ArrayList<>();
        String[] redEgg = getResources().getStringArray(R.array.red_box);
        String[] blueEgg = getResources().getStringArray(R.array.blue_box);
        String[] goldEgg = getResources().getStringArray(R.array.gold_box);
        eggItemArrayList.add(redEgg);
        eggItemArrayList.add(blueEgg);
        eggItemArrayList.add(goldEgg);

        //TODO 여기서 부터
        adapter = new AdapterRecyclerViewEgg(getActivity(), eggItemArrayList, userMoney, textViewMoney);
        eggRecyclerView.setAdapter(adapter);

        return  view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserDB();


    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDB();


    }

    void getUserDB(){
        SQLiteDatabase database_user;
        database_user = getActivity().openOrCreateDatabase("userDB.db", getActivity().MODE_PRIVATE, null);
        Cursor cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        try {
            cursor_user.moveToNext();
            userMoney = cursor_user.getInt(cursor_user.getColumnIndex("user_money"));
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("사용자 데이터 불러오기 오류").setPositiveButton("OK", null).show();
            getActivity().onBackPressed();
        }
    }

}
