package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    ViewPager2 viewPager;

    MainAdapter mainAdapter;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.main_view_paper);
        mainAdapter = new MainAdapter(this);
        viewPager.setAdapter(mainAdapter);

        Log.i("tag!!!",viewPager.getCurrentItem()+""); // 0page로 갔다가 1로 감
        // TODO Q.0번째 item으로 돌아가야는 0째 item에서 이동한 Activity라는 걸 어떻게 구분 하지..
        viewPager.setCurrentItem(1, false); //default page
    }

    //디바이스 뒤로가기 클릭 했을 때
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("EXIT").setMessage("나가시겠습니까?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed(); // 이 코드를 통해 꺼짐
                    }
        }).setNegativeButton("Cancel", null).create().show();
    }
}

