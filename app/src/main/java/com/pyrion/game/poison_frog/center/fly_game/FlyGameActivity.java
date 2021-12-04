package com.pyrion.game.poison_frog.center.fly_game;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pyrion.game.poison_frog.data.Frog;
import com.pyrion.game.poison_frog.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FlyGameActivity extends AppCompatActivity {
    boolean finish;

    SQLiteDatabase database_frog;
    int currentFrogKey;
    int currentFrogSize;

    final int maxProgressTime = 5000; //ms

    DisplayMetrics displaymetrics = new DisplayMetrics();
    Random random = new Random();
    Glide glide;

    TextView score;
    ImageView[] flyButtonArray;
    Timer[] flyTimerArray;
    ProgressBar progressBar;
    Timer timerGage = new Timer();


    int specialPoint;
    int normalPoint;
    int pointSum;


    int foodItem;
    int foodEffect;
    int currentFrogSpecies;
    String currentFrogName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_fly_game);
        ConstraintLayout layout = findViewById(R.id.layout);
        score = findViewById(R.id.score);
        progressBar=findViewById(R.id.progress);


        finish = false;
        specialPoint = 0;
        normalPoint = 0;
        pointSum = 0;


        //getCurrentFrogKey
        Intent intent = getIntent();
        currentFrogKey = intent.getIntExtra("currentFrogKey", 0);
        currentFrogName = intent.getStringExtra("currentFrogName");
        currentFrogSpecies = intent.getIntExtra("currentFrogSpecies", Frog.SPECIES_BASIC);
        currentFrogSize = intent.getIntExtra("currentFrogSize", 80);
        foodItem = intent.getIntExtra("food_item", 1)+4;
        foodEffect = intent.getIntExtra("food_effect", 1);

        flyButtonArray = new ImageView[foodItem];
        flyTimerArray = new Timer[foodItem];

        //add flyButton array
        for(int i = 0; i < flyButtonArray.length; i++) {
            flyButtonArray[i] = new ImageView(this);
            flyButtonArray[i].setId(i);
            ConstraintLayout.LayoutParams param = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            param.width = 160;
            param.height = 160;

            layout.addView(flyButtonArray[i], param);
            setContentView(layout);



            setSrc(flyButtonArray[i]);
            changFlyPosition(flyButtonArray[i]);
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

            if(finish){
                return;
            }
            ImageView iv = (ImageView)v;
            //TODO 사운드랑 진동 추가

            if (iv.getTag().equals("special")) {
                specialPoint += 3*foodEffect;
                pointSum += 3*foodEffect;
            } else {
                normalPoint +=1*foodEffect;
                pointSum += 1*foodEffect;
            }

            score.setText(pointSum + "");

//            int key = getIdKey(v.getId());
            int key = v.getId();
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
        final int randomDegree = random.nextInt(360)-180;

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
        int i = random.nextInt(5);
        if(i==4){
            glide.with(FlyGameActivity.this).load( R.drawable.special_bubble).into(targetFlyIV);
            targetFlyIV.setTag("special");
        }else{
            glide.with(FlyGameActivity.this).load( R.drawable.normal_bubble).into(targetFlyIV);
            targetFlyIV.setTag("");
        }
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
                                progressBar.setProgress(currprog);
                            } else if (currprog == 0) {
                                //end of Game

                                for(int i = 0; i < flyButtonArray.length; i++) {
                                    //stop all thread
                                    flyTimerArray[i].cancel();
                                }

                                if(finish==false){
                                    //end of the game
                                    finish = true;

                                    pointDataUpdate();
                                    timerGage.cancel();
                                    showPointAlert();
                                }

                            }

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
    protected void onDestroy() {
        Log.i("onDestroy", "flygame destroied");
        super.onDestroy();
//       점수를 보여주고 게임이 완전 종료됩니다.
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


    void  showPointAlert(){
        //sell house 얼럿 다이어로그
        LayoutInflater inflater = LayoutInflater.from(this);
        View pointSumView = inflater.inflate(R.layout.alert_is_sell_house, null);

        AlertDialog.Builder sumPointBuilder = new AlertDialog.Builder(this);
        sumPointBuilder.setView(pointSumView);
        AlertDialog pointAlertDialog = sumPointBuilder.create();

        //다이얼로그의 바깥쪽 영역을 터치했을때 다이얼로그가 사라지지 않도록
        pointAlertDialog.setCanceledOnTouchOutside(false);

        //button setting
        ImageView bigFrogSrc = pointSumView.findViewById(R.id.iv);
        ImageView cancelBtn = pointSumView.findViewById(R.id.cancel_button);
        TextView btn = pointSumView.findViewById(R.id.tv_sell);

        ImageView specialBubbleSrc = pointSumView.findViewById(R.id.iv_power);
        ImageView normalBubbleSrc = pointSumView.findViewById(R.id.house_src);
        TextView tvSpecialPoint = pointSumView.findViewById(R.id.tv_power);
        TextView tvNormalPoint = pointSumView.findViewById(R.id.house_price);
        TextView tvSumPrice= pointSumView.findViewById(R.id.sum);
        TextView frogName = pointSumView.findViewById(R.id.frog_name);
        cancelBtn.setVisibility(View.INVISIBLE);


        bigFrogSrc.setImageResource(currentFrogSpecies);
        frogName.setText(currentFrogName);
        btn.setText("확인");

        normalBubbleSrc.setImageResource(R.drawable.normal_bubble);
        specialBubbleSrc.setImageResource(R.drawable.special_bubble);

        tvSpecialPoint.setText(specialPoint+" 점");
        tvNormalPoint.setText(normalPoint+" 점");
        tvSumPrice.setText("+"+ pointSum+"사이즈");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointAlertDialog.cancel();
                Toast.makeText(FlyGameActivity.this, "+"+pointSum+"Size", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
        pointAlertDialog.show();
    }

    @Override
    public void onBackPressed() {
        timerGage.cancel();
        super.onBackPressed();
    }
    
}

