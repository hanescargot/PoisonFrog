package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    ViewPager2 viewPager;

    MainAdapterCenter mainAdapterCenter;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.main_view_paper);
        mainAdapterCenter = new MainAdapterCenter(this);
        viewPager.setAdapter(mainAdapterCenter);

        Log.i("tag!!!",viewPager.getCurrentItem()+""); // 0page로 갔다가 1로 감
        viewPager.setCurrentItem(1, false); //default page
        // TODO Q.0번째 item으로 돌아가야는 0째 item에서 이동한 Activity라는 걸 어떻게 구분 하지..
    }


}

