package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pyrion.poison_frog.data.Frog;

import java.util.Random;

public class IntroActivity extends AppCompatActivity {

    TextView title;
    ImageView imageView;

    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        title = findViewById(R.id.title);
        imageView = findViewById(R.id.iv_power);
        Glide.with(this).load(Frog.getFrogSpecies(random.nextInt(Frog.FROG_SPECIES_COUNT))).into(imageView);
        Animation animation_logo = AnimationUtils.loadAnimation(this, R.anim.intro_logo);
        imageView.startAnimation(animation_logo);

        Animation animation_title = AnimationUtils.loadAnimation(this, R.anim.intro_title);
        title.startAnimation(animation_title);


        animation_logo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

}