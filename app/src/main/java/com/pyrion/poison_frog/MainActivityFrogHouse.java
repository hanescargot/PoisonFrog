package com.pyrion.poison_frog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivityFrogHouse extends AppCompatActivity {

    ArrayList<OneFrogSet> frogSet = new ArrayList<>();
    MainAdapterFrogHouse adapter;
    ListView listView;

    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frog_house);

        //actionbar setting
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Frog House");
        actionbar.setDisplayHomeAsUpEnabled(true); //show back button

        database= openOrCreateDatabase("frogsDB.db", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS frogs_data_set("
                +"num_key INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"house_type INTEGER,"
                +" creator_name  VARCHAR(40),"
                +"  frog_name VARCHAR(40),"
                +" frog_state INTEGER,"
                +" frog_property INTEGER,"
                +" frog_size DOUBLE,"
                +" frog_power DOUBLE)");

        Cursor cursor= database.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
        if(cursor==null) return;

        while(cursor.moveToNext()){//[레코드:row]로 커서이동
            //columnIndex: 0 is origin number
            int house_type = cursor.getInt(1);
            String creator_name = cursor.getString(2);
            String frog_name = cursor.getString(3);
            int frog_state = cursor.getInt(4);
            int frog_property = cursor.getInt(5);
            int frog_size = cursor.getInt(6);
            int frog_power = cursor.getInt(7);

            frogSet.add(new OneFrogSet(
                    house_type,
                    creator_name,
                    frog_name,
                    frog_state,
                    frog_property,
                    frog_size,
                    frog_power));
        }

        adapter = new MainAdapterFrogHouse( this, frogSet);

        listView= findViewById(R.id.house_activity);
        //리스트뷰에게 아답터 설정
        listView.setAdapter(adapter);
    }

}