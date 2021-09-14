package com.pyrion.poison_frog.Fight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.pyrion.poison_frog.IntroActivity;
import com.pyrion.poison_frog.MainActivity;
import com.pyrion.poison_frog.R;

import java.util.Random;

public class ActivityMatch extends AppCompatActivity {
    int extraTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        TextView title = findViewById(R.id.title);
        ImageView imageView = findViewById(R.id.iv_power);
        //트윈 애니메이션

        Animation animation_logo = AnimationUtils.loadAnimation(this, R.anim.matching);
        imageView.startAnimation(animation_logo);

        Random random = new Random();
        extraTime = random.nextInt(500);
        startLoading();
    }


    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ActivityMatch.this, ActivityFight.class);
                startActivity(intent);
                finish();
            }
        }, 2500+extraTime);
    }

}