package com.pyrion.game.poison_frog.Fight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pyrion.poison_frog.R;

import java.util.Random;

public class ActivityMatch extends AppCompatActivity {
    int extraTime = 0;
    Intent previousIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        previousIntent = getIntent();
        int currentFrogSpecies = previousIntent.getIntExtra("currentFrogSpecies", 0);
        ImageView imageView = findViewById(R.id.iv_power);
        Glide.with(this).load(currentFrogSpecies).into(imageView);

        Animation animation_logo = AnimationUtils.loadAnimation(this, R.anim.matching);
        imageView.startAnimation(animation_logo);

        Random random = new Random();
        extraTime = random.nextInt(500);
        startLoading();
    }

    @Override
    public void onBackPressed() {
        // 들어온 이상 나갈 수 없어
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ActivityMatch.this, ActivityFight.class);
                intent.putExtra("currentFrogKey", previousIntent.getIntExtra("currentFrogKey", 0));
                startActivity(intent);
                finish();

            }
        }, 2500+extraTime);
    }


}