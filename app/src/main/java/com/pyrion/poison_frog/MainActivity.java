package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    ViewPager2 viewPager;

    MainAdapter mainAdapter;
    Intent intent;

    int fragmentNavigation = 1;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.main_view_paper);
        mainAdapter = new MainAdapter(this);
        viewPager.setAdapter(mainAdapter);

        viewPager.setCurrentItem(fragmentNavigation, false); //default page
        Toast.makeText(this, getPackageName()+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        intent = getIntent();
        fragmentNavigation = intent.getIntExtra("fragment_navigation", 1);
        viewPager.setCurrentItem(fragmentNavigation, false);

        getIntent().removeExtra("fragment_navigation");
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


    public void updateView() {
        viewPager.setCurrentItem(fragmentNavigation, false);
    }
}

