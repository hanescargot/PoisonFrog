package com.pyrion.game.poison_frog.egg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.pyrion.poison_frog.R;

public class ActivityFrogBook extends AppCompatActivity {
    AdapterFrogBook adapter;
    RecyclerView recyclerView;
    CustomGridLayoutManager layoutManager;
    SnapHelper snapHelper;

    ImageButton leftBtn, rightBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frog_book);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33333333")));

        leftBtn = findViewById(R.id.left_button);
        rightBtn = findViewById(R.id.right_button);
        leftBtn.setVisibility(View.INVISIBLE);

        String[] speciesList = getResources().getStringArray(R.array.species_explain);
        adapter = new AdapterFrogBook( this, speciesList, leftBtn, rightBtn);

        layoutManager = new CustomGridLayoutManager(this);
        recyclerView= findViewById(R.id.recyclerview_book);
        recyclerView.setLayoutManager(layoutManager);

        //넘어가는 효과
        layoutManager.setScrollEnabled(false);
//        snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);


        //리스트뷰에게 아답터 설정
        recyclerView.setAdapter(adapter);

    }

    int pos;
    public void cancel(View view) {
        onBackPressed();
    }

    public void left(View view) {
        pos =((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

        //
        layoutManager.scrollToPosition(pos-1);
        int position = pos-1;

        leftBtn.setVisibility(View.VISIBLE);
        rightBtn.setVisibility(View.VISIBLE);
        if( position == 0){
            leftBtn.setVisibility(View.INVISIBLE);
        }
        if(position == layoutManager.getItemCount()-1 ) {
            rightBtn.setVisibility(View.INVISIBLE);
        }

    }

    public void right(View view) {
        pos =((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

        //
        layoutManager.scrollToPosition(pos+1);
        int position = pos+1;

        leftBtn.setVisibility(View.VISIBLE);
        rightBtn.setVisibility(View.VISIBLE);
        if( position == 0){
            leftBtn.setVisibility(View.INVISIBLE);
        }
        if(position == layoutManager.getItemCount()-1 ) {
            rightBtn.setVisibility(View.INVISIBLE);
        }

    }
}

class CustomGridLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}