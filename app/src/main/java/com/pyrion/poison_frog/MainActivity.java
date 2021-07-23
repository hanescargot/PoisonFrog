package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;



public class MainActivity extends AppCompatActivity {
    ViewPager2 viewPager;

    MainPaperAdapter mainPaperAdapter;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.main_view_paper);
        mainPaperAdapter = new MainPaperAdapter(this);
        viewPager.setAdapter(mainPaperAdapter);
    }
}

