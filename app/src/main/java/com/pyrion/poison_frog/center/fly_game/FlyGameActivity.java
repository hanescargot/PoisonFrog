package com.pyrion.poison_frog.center.fly_game;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.pyrion.poison_frog.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FlyGameActivity extends AppCompatActivity {
    SQLiteDatabase database_frog;
    int currentFrogKey;
    int currentFrogSize;

    final int maxProgressTime = 5000; //ms

    DisplayMetrics displaymetrics = new DisplayMetrics();
    Random random = new Random();
    Glide glide;

    TextView score;
    ImageView flyButton;
    ImageView[] flyButtonArray = new ImageView[5];
    Timer[] flyTimerArray = new Timer[5];
    ProgressBar progressBar;
    Timer timerGage = new Timer();

    int pointSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fly_game);
        flyButton = findViewById(R.id.target_fly01);
        score = findViewById(R.id.score);
        progressBar=findViewById(R.id.progress);


        //getCurrentFrogKey
        Intent intent = getIntent();
        currentFrogKey = intent.getIntExtra("currentFrogKey", 0);
        currentFrogSize = intent.getIntExtra("currentFrogSize", 40);
        Log.i("point", currentFrogSize+"");

        //add flyButton array
        for(int i = 0; i < flyButtonArray.length; i++) {
            flyButtonArray[i] = findViewById(R.id.target_fly01+i);
            setSrc(flyButtonArray[i]);
            moveFly(flyButtonArray[i]);
            flyButtonArray[i].setOnClickListener(flyButtonClicked);
            flyTimerArray[i] = newFlyTimer(flyButtonArray[i]);
        }

        //game Start
        progressBar.setProgress(maxProgressTime);
        startTimerThread();

    }//OnCreate


    View.OnClickListener flyButtonClicked = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ImageView iv = (ImageView)v;
            //TODO 사운드랑 진동 추가

            if (iv.getTag().equals(R.drawable.fly_fly01)) {
                pointSum += 5;
            } else {
                pointSum++;
            }

            score.setText(pointSum + "");
            iv.setImageResource(R.drawable.main_dollar);
            iv.setClickable(false);

            int key = getIdKey(v.getId());
            flyTimerArray[key].cancel();

            setSrc(iv);
            changFlyPosition(iv);
            flyTimerArray[key] = newFlyTimer(iv);
        }
    };

    Timer newFlyTimer(ImageView targetFlyIV){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { //UI 수정 가능하도록
                    @Override
                    public void run() {
                        targetFlyIV.setClickable(true);
                        moveFly(targetFlyIV);
                    }
                });
            }
        }, random.nextInt(1000), 1000);

        return timer;
    }

    void moveFly(ImageView targetFlyIV){

        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final float dx = random.nextFloat() * displaymetrics.widthPixels;
        final float dy = random.nextFloat() * displaymetrics.heightPixels;
        final int randomDegree = random.nextInt(360);
        targetFlyIV.animate()
                .x(dx)
                .y(dy)
                .rotation(randomDegree)
                .setDuration(1000)
                .start();
    }
    void changFlyPosition(ImageView targetFlyIV){

        //out position

        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dx = getSownPlaceX(displaymetrics.widthPixels, random.nextInt(2));
        float dy = random.nextFloat() * displaymetrics.heightPixels;
        final int randomDegree = random.nextInt(360);
        targetFlyIV.animate()
                .x(dx)
                .y(dy)
                .rotation(randomDegree)
                .setDuration(0)
                .start();
    }



    void setSrc( ImageView targetFlyIV) {
        targetFlyIV.setClickable(true);
        int i = random.nextInt(6);
        glide.with(FlyGameActivity.this).load( R.drawable.fly_fly01+i).into(targetFlyIV);
        targetFlyIV.setTag(R.drawable.fly_fly01+i);
    }




    public void startTimerThread(){
        TimerTask timerTask = new TimerTask(){ //timerTask는 timer가 일할 내용을 기록하는 객체
            @Override
            public void run() {
                decreaseBar();
            }

        };

//        Timer timerGage= new Timer(); //timer생성
        timerGage.schedule(timerTask, 0,1); //timerTask라는 일을 갖는 timer를 0초딜레이로 1000ms마다 실행

    }


    private Handler handler;
    private Runnable runnable;

    public void decreaseBar() {
        runOnUiThread( //progressBar는 ui에 해당하므로 runOnUiThread로 컨트롤해야한다
                new Runnable() { //thread구동과 마찬가지로 Runnable을 써주고

                    @Override
                    public void run() { //run을 해준다. 그러나 일반 thread처럼 .start()를 해줄 필요는 없다

                            int currprog = progressBar.getProgress();

                            if (currprog > 0) {
                                currprog = currprog - 1;
                            } else if (currprog == 0) {
                                //end of Game

                                for(int i = 0; i < flyButtonArray.length; i++) {
                                    //stop all thread
                                    flyTimerArray[i].cancel();
                                }

                                //end of the game


                                onBackPressed();

                            }
                            progressBar.setProgress(currprog);
                    }
                }

        );

    }

    private void pointDataUpdate() {
        database_frog = this.openOrCreateDatabase("frogsDB.db", this.MODE_PRIVATE, null);
        database_frog.execSQL("UPDATE frogs_data_set SET"
                +" frog_size =" +(currentFrogSize + pointSum)
                +" WHERE frog_key ="+currentFrogKey
        );

    }

    @Override
    public void onBackPressed() {
        pointDataUpdate();
        timerGage.cancel();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.i("onDestroy", "flygame destroied");
        super.onDestroy();
//       점수를 보여주고 게임이 완전 종료됩니다.
    }

    int getIdKey(int id){
        switch (id){
            case R.id.target_fly01:
                return 0;
            case R.id.target_fly02:
                return 1;
            case R.id.target_fly03:
                return 2;
            case R.id.target_fly04:
                return 3;
            case R.id.target_fly05:
                return 4;

        }
        return -1;
    }

    int getSownPlaceX(int pixel,int randomNum ){
        Log.i("random", randomNum+"");
        switch (randomNum){
            case 0:
                return pixel+200;
            case 1:
                return -200;
        }
        return 0;
    }


}

