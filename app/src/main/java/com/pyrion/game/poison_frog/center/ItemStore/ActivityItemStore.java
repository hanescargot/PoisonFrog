package com.pyrion.game.poison_frog.center.ItemStore;

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

import com.pyrion.game.poison_frog.data.OneItemSet;
import com.pyrion.game.poison_frog.R;

import java.util.ArrayList;

public class ActivityItemStore extends AppCompatActivity {

    ArrayList<OneItemSet> itemDataArrayList = new ArrayList<>();
    AdapterItemStore adapter;
    ListView listView;
    Cursor cursor_item;
    SQLiteDatabase database_item;

    Intent intent;
    int currentUserMoney;

    String[] itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_store);


    }

    @Override
    protected void onResume() {
        super.onResume();

        intent = getIntent();
        currentUserMoney = intent.getIntExtra("user_money", 0);

        //actionbar setting
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("ITEM STORE");
        actionbar.setDisplayHomeAsUpEnabled(true); //show back button

        itemDataArrayList.clear();
        database_item = openOrCreateDatabase("itemDB.db", MODE_PRIVATE, null);
        cursor_item= database_item.rawQuery("SELECT * FROM item_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
        if(cursor_item!=null) {
            while (cursor_item.moveToNext()) {//[레코드:row]로 커서이동
                String ItemName = cursor_item.getString(cursor_item.getColumnIndex("item_name"));
                String ItemExplain = cursor_item.getString(cursor_item.getColumnIndex("item_explain"));
                int CurrentItemPrice = cursor_item.getInt(cursor_item.getColumnIndex("current_item_price"));
                int CurrentLevel = cursor_item.getInt(cursor_item.getColumnIndex("current_level"));
                int MaxLevel = cursor_item.getInt(cursor_item.getColumnIndex("max_level"));
                Double UpgradePriceTimes = cursor_item.getDouble(cursor_item.getColumnIndex("upgrade_price_times"));
                String ItemCase = cursor_item.getString(cursor_item.getColumnIndex("item_case"));

                itemDataArrayList.add(new OneItemSet(
                                ItemName,
                                ItemExplain,
                                CurrentItemPrice,
                                CurrentLevel,
                                MaxLevel,
                                UpgradePriceTimes,
                                ItemCase
                        )
                );
            }
        }


        adapter = new AdapterItemStore( this, itemDataArrayList, currentUserMoney);

        listView= findViewById(R.id.activity_item_store);
        //리스트뷰에게 아답터 설정
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //상세 아이템 정보
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