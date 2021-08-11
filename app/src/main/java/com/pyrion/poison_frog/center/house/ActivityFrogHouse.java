package com.pyrion.poison_frog.center.house;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pyrion.poison_frog.MainActivity;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.OneFrogSet;
import com.pyrion.poison_frog.R;

import java.util.ArrayList;

public class ActivityFrogHouse extends AppCompatActivity {

    ArrayList<OneFrogSet> oneFrogSetList = new ArrayList<>();
    AdapterFrogHouse adapter;
    ListView listView;
    Cursor cursor_frog;
    SQLiteDatabase database_frog;
    SQLiteDatabase database_user;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frog_house);

        //actionbar setting
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Frog House");
        actionbar.setDisplayHomeAsUpEnabled(true); //show back button

        //TODO intent로 안받고 db나 static으로 받아도 될까?
        oneFrogSetList.clear();
        database_frog = openOrCreateDatabase("frogsDB.db", MODE_PRIVATE, null);
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
            }
        }

        //add buy new house event
        oneFrogSetList.add(new OneFrogSet());

        adapter = new AdapterFrogHouse( this, oneFrogSetList);

        listView= findViewById(R.id.house_activity);
        //리스트뷰에게 아답터 설정
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OneFrogSet selectedFrogSet = oneFrogSetList.get(position);
                int newFrogKey = selectedFrogSet.getFrogKey();

                if(selectedFrogSet.getHouseType()== Frog.HOUSE_TYPE_BUY_NEW){
                    //TODO  Buy New Frog house
                    database_frog.execSQL("INSERT INTO frogs_data_set(house_type, creator_name, frog_name, frog_state, frog_species, frog_size, frog_power) VALUES('"
                            + Frog.HOUSE_TYPE_LENT + "','"
                            + Frog.USER_NAME_NULL + "','"
                            + Frog.FROG_NAME_NULL + "','"
                            + Frog.STATE_ALIVE + "','"
                            + Frog.SPECIES_BASIC + "','"
                            + Frog.SIZE_DEFAULT + "','"
                            + Frog.POWER_DEFAULT + "')"
                    );
                    cursor_frog.moveToLast();
                    newFrogKey = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_key"));
                }


                database_user = openOrCreateDatabase("userDB.db", MODE_PRIVATE, null);
                database_user.execSQL("UPDATE user_data_set SET"
                        +" selected_frog_key = " + newFrogKey
                );

                intent = new Intent(ActivityFrogHouse.this, MainActivity.class);
                intent.putExtra("fragment_navigation", 1);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}