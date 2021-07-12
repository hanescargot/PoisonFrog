package com.pyrion.poison_frog;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Random;

public class FlyGameActivity extends AppCompatActivity {

    Random random = new Random();

    int targetFlyResNum;
    ImageView targetFlyIV, censoredIV, gameOverIV;

    int[] flysResNum = new int[9];
    ImageView[] flysIV = new ImageView[9];

    HashMap<String, Integer> flysTagHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameOverIV = findViewById(R.id.game_over);
        targetFlyIV = findViewById(R.id.target_fly);
        censoredIV = findViewById(R.id.censored);
        //맵 생성
        for(int count = 0; count< flysIV.length; count++ ) {
            flysIV[count] = findViewById(R.id.fly1 + count);
            flysResNum[count] = random.nextInt(6);
            flysIV[count].setImageResource(R.drawable.fly_fly01 + flysResNum[count]);

            String key = flysIV[count].getTag().toString();
            flysTagHashMap.put(key, flysResNum[count]);

        }
        setTarget();

    }

    public void flyButtonListener(View v){
            String tag = v.getTag().toString();
            if(flysTagHashMap.get(tag)== targetFlyResNum){
                v.setVisibility(View.INVISIBLE);
                flysTagHashMap.remove(tag);
            }else{
                //TODO set Game Over
                for(ImageView id : flysIV){
                    id.setVisibility(View.INVISIBLE);
                }
                gameOverIV.setVisibility(View.VISIBLE);

            }
            if( !flysTagHashMap.containsValue(targetFlyResNum) ) {
                
                if (flysTagHashMap.size()==0){
                    censoredIV.setVisibility(View.VISIBLE);
                    //TODO 게임 finish : 보상 Image
                }else{
                    setTarget();
                }
            }
    }

    void setTarget() {
        targetFlyResNum = flysTagHashMap.values().iterator().next();
        targetFlyIV.setImageResource(R.drawable.fly_fly01 + targetFlyResNum);
    }
}