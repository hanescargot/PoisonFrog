package com.pyrion.poison_frog.egg;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.pyrion.poison_frog.R;

public class ActivityFrogBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frog_book);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33333333")));

    }
}