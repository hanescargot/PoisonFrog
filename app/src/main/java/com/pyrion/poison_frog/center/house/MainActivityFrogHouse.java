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

public class MainActivityFrogHouse extends AppCompatActivity {

    ArrayList<OneFrogSet> frogSetList = new ArrayList<>();
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

        Cursor cursor_frog= database.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
        if(cursor_frog==null) return;

        while(cursor_frog.moveToNext()){//[레코드:row]로 커서이동
            //columnIndex: 0 is origin number
            int house_type = cursor_frog.getInt(cursor_frog.getColumnIndex("house_type"));
            String creator_name = cursor_frog.getString(cursor_frog.getColumnIndex("creator_name"));
            String frog_name = cursor_frog.getString(cursor_frog.getColumnIndex("frog_name"));
            int frog_state = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_state"));
            int frog_species = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_species"));
            int frog_size = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_size"));
            int frog_power = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_power"));

            frogSetList.add(new OneFrogSet(
                    house_type,
                    creator_name,
                    frog_name,
                    frog_state,
                    frog_species,
                    frog_size,
                    frog_power));
        }
        cursor_frog.close();

        adapter = new MainAdapterFrogHouse( this, frogSetList);

        listView= findViewById(R.id.house_activity);
        //리스트뷰에게 아답터 설정
        listView.setAdapter(adapter);
    }

}