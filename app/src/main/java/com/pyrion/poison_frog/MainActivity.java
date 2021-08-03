package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.util.Log;

import com.pyrion.poison_frog.center.AdapterCenter;


public class MainActivity extends AppCompatActivity {
    ViewPager2 viewPager;

    AdapterCenter adapterCenter;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.main_view_paper);
        adapterCenter = new AdapterCenter(this);
        viewPager.setAdapter(adapterCenter);

        Log.i("tag!!!",viewPager.getCurrentItem()+""); // 0page로 갔다가 1로 감
        // TODO Q.0번째 item으로 돌아가야는 0째 item에서 이동한 Activity라는 걸 어떻게 구분 하지..
        viewPager.setCurrentItem(1, false); //default page
    }


}

