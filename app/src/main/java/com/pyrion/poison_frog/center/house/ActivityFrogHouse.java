package com.pyrion.poison_frog.center.house;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import com.pyrion.poison_frog.data.OneFrogSet;
import com.pyrion.poison_frog.R;

import java.util.ArrayList;

public class ActivityFrogHouse extends AppCompatActivity {

    ArrayList<OneFrogSet> frogSetList = new ArrayList<>();
    AdapterFrogHouse adapter;
    ListView listView;
    Cursor cursor_frog;
    SQLiteDatabase database_frog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frog_house);

        //actionbar setting
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Frog House");
        actionbar.setDisplayHomeAsUpEnabled(true); //show back button

        //TODO intent로 안받고 db나 static으로 받아도 될까?
        database_frog = openOrCreateDatabase("frogsDB.db", MODE_PRIVATE, null);
        cursor_frog= database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
        while(cursor_frog.moveToNext()){//[레코드:row]로 커서이동
            int frog_key = cursor_frog.getInt(cursor_frog.getColumnIndex("selected_frog_key"));
            int house_type = cursor_frog.getInt(cursor_frog.getColumnIndex("house_type"));
            String creator_name = cursor_frog.getString(cursor_frog.getColumnIndex("creator_name"));
            String frog_name = cursor_frog.getString(cursor_frog.getColumnIndex("frog_name"));
            int frog_state = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_state"));
            int frog_species = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_species"));
            int frog_size = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_size"));
            int frog_power = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_power"));

            frogSetList.add(new OneFrogSet(
                    frog_key,
                    house_type,
                    creator_name,
                    frog_name,
                    frog_state,
                    frog_species,
                    frog_size,
                    frog_power));
        }

        cursor_frog.close();
        //add function of to get a new house
        adapter = new AdapterFrogHouse( this, frogSetList);

        listView= findViewById(R.id.house_activity);
        //리스트뷰에게 아답터 설정
        listView.setAdapter(adapter);
    }

}