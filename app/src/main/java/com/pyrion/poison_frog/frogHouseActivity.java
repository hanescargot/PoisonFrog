package com.pyrion.poison_frog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class frogHouseActivity extends AppCompatActivity {

    ArrayList<oneFrogSet> oneFrogSet= new ArrayList<>();
    customAdapter adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frog_house);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Frog House");
        actionbar.setDisplayHomeAsUpEnabled(true);


        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog", "fire", 80, 80));
        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog2", "fire", 80, 80));
        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog", "fire", 80, 80));
        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog2", "fire", 80, 80));
        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog", "fire", 80, 80));
        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog2", "fire", 80, 80));
        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog", "fire", 80, 80));
        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog2", "fire", 80, 80));
        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog", "fire", 80, 80));
        oneFrogSet.add( new oneFrogSet(R.drawable.main_frog_jelly, "Hyunju",
                "퐁퐁Frog2", "fire", 80, 80));


        adapter = new customAdapter( this, oneFrogSet);

        listView= findViewById(R.id.house_activity);
        //리스트뷰에게 아답터 설정
        listView.setAdapter(adapter);
    }
}