package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class IntroActivity extends AppCompatActivity {

    TextView title;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        title = findViewById(R.id.title);
        imageView = findViewById(R.id.frog_src);
        //트윈 애니메이션

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